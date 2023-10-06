package once;

/**
 * @program: bisheBackend-master
 * @description: 用来读excel
 * @author: HenryYang
 * @create: 2023-03-14 10:37
 **/

import com.alibaba.excel.EasyExcel;

import java.util.List;

/**
 * 最简单的读
 * <p>
 * 1. 创建excel对应的实体对象 参照{@link XingqiuUserInfo}
 * <p>
 * 2. 由于默认一行行的读取excel，所以需要创建excel一行一行的回调监听器，参照{@link XingqiuUserInfoListener}
 * <p>
 * 3. 直接读即可
 */
public class ReadExcel {

    public static void main(String[] args) {

        // 写法1：JDK8+ ,不用额外写一个DemoDataListener
        // since: 3.0.0-beta1
        String fileName = "xxxx";
        synchronousRead(fileName);
        simpleListenerRead(fileName);
//        缺少数据

    }
    public static void simpleListenerRead(String fileName){
        // 这里默认每次会读取100条数据 然后返回过来 直接调用使用数据就行
        // 具体需要返回多少行可以在`PageReadListener`的构造函数设置
        EasyExcel.read(fileName, XingqiuUserInfo.class, new XingqiuUserInfoListener())
                .sheet().doRead();
    }
    /**
     * 同步的返回，不推荐使用，如果数据量大会把数据放到内存里面
     */
    public static void synchronousRead(String fileName) {
        List<XingqiuUserInfo> list = EasyExcel.read(fileName).head(XingqiuUserInfo.class).sheet().doReadSync();
        for (XingqiuUserInfo userInfo:
             list) {
            System.out.println(userInfo);
        }
    }
}