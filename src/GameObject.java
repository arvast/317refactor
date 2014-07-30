final class GameObject extends Animable {

    public Model getRotatedModel()
    {
        int animationId = -1;
        if(animation != null)
        {
            int step = client.loopCycle - nextFrameTime;
            if(step > 100 && animation.frameStep > 0)
                step = 100;
            while(step > animation.getFrameLength(frame))
            {
                step -= animation.getFrameLength(frame);
                frame++;
                if(frame < animation.frameCount)
                    continue;
                frame -= animation.frameStep;
                if(frame >= 0 && frame < animation.frameCount)
                    continue;
                animation = null;
                break;
            }
            nextFrameTime = client.loopCycle - step;
            if(animation != null)
                animationId = animation.frame2Ids[frame];
        }
        GameObjectDefinition definition;
        if(childrenIds != null)
            definition = getChildDefinition();
        else
            definition = GameObjectDefinition.forID(objectId);
        if(definition == null)
        {
            return null;
        } else
        {
            return definition.getModelAt(type, orientation, vertexHeightBottomLeft, vertexHeightBottomRight, vertexHeightTopRight, vertexHeightTopLeft, animationId);
        }
    }

    private GameObjectDefinition getChildDefinition()
    {
        int child = -1;
        if(varBitId != -1)
        {
            VarBit varBit = VarBit.cache[varBitId];
            int configId = varBit.configId;
            int lsb = varBit.leastSignificantBit;
            int msb = varBit.mostSignificantBit;
            int bit = client.BITFIELD_MAX_VALUE[msb - lsb];
            child = clientInstance.variousSettings[configId] >> lsb & bit;
        } else
        if(configId != -1)
            child = clientInstance.variousSettings[configId];
        if(child < 0 || child >= childrenIds.length || childrenIds[child] == -1)
            return null;
        else
            return GameObjectDefinition.forID(childrenIds[child]);
    }

    public GameObject(int objectId, int orientation, int type, int vertexHeightBottomRight, int vertexHeightTopRight, int vertexHeightBottomLEft,
                         int vertexHeightTopLeft, int animationId, boolean animating)
    {
        this.objectId = objectId;
        this.type = type;
        this.orientation = orientation;
        this.vertexHeightBottomLeft = vertexHeightBottomLEft;
        this.vertexHeightBottomRight = vertexHeightBottomRight;
        this.vertexHeightTopRight = vertexHeightTopRight;
        this.vertexHeightTopLeft = vertexHeightTopLeft;
        if(animationId != -1)
        {
            animation = AnimationSequence.anims[animationId];
            frame = 0;
            nextFrameTime = client.loopCycle;
            if(animating && animation.frameStep != -1)
            {
                frame = (int)(Math.random() * (double) animation.frameCount);
                nextFrameTime -= (int)(Math.random() * (double) animation.getFrameLength(frame));
            }
        }
        GameObjectDefinition definition = GameObjectDefinition.forID(this.objectId);
        varBitId = definition.varBitId;
        configId = definition.configIds;
        childrenIds = definition.childrenIds;
    }

    private int frame;
    private final int[] childrenIds;
    private final int varBitId;
    private final int configId;
    private final int vertexHeightBottomLeft;
    private final int vertexHeightBottomRight;
    private final int vertexHeightTopRight;
    private final int vertexHeightTopLeft;
    private AnimationSequence animation;
    private int nextFrameTime;
    public static client clientInstance;
    private final int objectId;
    private final int type;
    private final int orientation;
}
