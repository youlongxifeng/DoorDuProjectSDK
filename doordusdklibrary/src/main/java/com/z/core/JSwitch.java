package com.z.core;
/**
 * 位开关类 目前只支持32个开关即int的长度
 * @author admin
 *
 */
public class JSwitch {

    protected int mFlag;

    public JSwitch(){}
    public JSwitch(int flag)
    {
        this.mFlag = flag;
    }
    /**
     * 打开开关
     * @param flag
     */
    public final void add(int flag)
    {
        mFlag |= flag;
    }
    /**
     * 移除指定的开关,即关闭开关
     * @param flag
     */
    public final void remove(int flag)
    {
        mFlag &= (~flag);
    }
    /**
     * 是否有指定的某向开关
     * @param flag
     * @return
     */
    public final boolean has(int flag)
    {
        return (mFlag & flag) != 0;
    }

    @Override
    public String toString() {
        // TODO Auto-generated method stub
        return String.format("SWITCH flag %d",mFlag);
    }
    /**
     * 重置开关值为新值
     * @param flag
     */
    public void reset(int flag)
    {
        mFlag = flag;
    }

    public final int getFlag()
    {
        return mFlag;

    }
    /**
     * 判断是否有开关
     * @return ture 无,false有指定开关
     */
    public boolean zero()
    {
        return 0 == mFlag;
    }
}
