
package dong.lan.base.ui;

/**
 * description: 用于延迟加载的接口,例如首页初始化完用户信息后,才进行其他数据的初始化
 */

public interface DelayInitView<T> {

    void start(T data);
}
