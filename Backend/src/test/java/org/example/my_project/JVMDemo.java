package org.example.my_project;

import java.util.ArrayList;
import java.util.List;

/**
 * JVM 实操演示程序（自动版）
 * 每步自动暂停 15 秒，给你时间去终端跑 jstat/jmap 观察
 */
public class JVMDemo {

    private static final String DEMO_NAME = "JVM 内存模型演示";

    public static void main(String[] args) throws Exception {
        // 允许通过参数自定义暂停秒数
        int pause = args.length > 0 ? Integer.parseInt(args[0]) : 15;

        String pid = ProcessHandle.current().pid() + "";
        System.out.println("========================================");
        System.out.println("  JVM 内存模型 & GC 实操演示");
        System.out.println("  PID: " + pid + "   JDK: " + Runtime.version());
        System.out.println("  每步暂停 " + pause + " 秒，去另一个终端跑命令观察");
        System.out.println("========================================");
        System.out.println();
        System.out.println("在另一个终端运行：");
        System.out.println("  jstat -gc " + pid + " 1s");
        System.out.println("  （每 1 秒刷新一次 GC 统计）");
        System.out.println();

        // ===== 步骤1：初始状态 =====
        printStep(1, "初始堆状态");
        printHeap("初始");
        sleep(pause);

        // ===== 步骤2：分配对象，Eden 增长 =====
        printStep(2, "分配 300 个对象（每个 100KB）-> 观察 Eden 区增长");
        List<byte[]> list = new ArrayList<>();
        for (int i = 0; i < 300; i++) {
            list.add(new byte[1024 * 100]);
        }
        printHeap("分配 30MB 后");
        sleep(pause);

        // ===== 步骤3：再分配 300 个触发 Minor GC =====
        printStep(3, "再分配 300 个对象 -> Eden 满了 -> 触发 Minor GC");
        for (int i = 0; i < 300; i++) {
            list.add(new byte[1024 * 100]);
        }
        printHeap("Minor GC 后");
        sleep(pause);

        // ===== 步骤4：丢引用 + 手动 GC =====
        printStep(4, "丢掉一半对象引用，然后 System.gc()");
        list.subList(0, 300).clear();
        System.out.println("  已 clear() 前 300 个引用，这些对象变成垃圾");
        printHeap("clear 之后（垃圾还在）");
        sleep(5);

        System.out.println("  触发 System.gc() ...");
        System.gc();
        Thread.sleep(2000);
        printHeap("GC 之后（垃圾被回收）");
        sleep(pause);

        // ===== 步骤5：创建大对象 =====
        printStep(5, "分配大对象（8MB）-> 直接进入老年代");
        @SuppressWarnings("unused")
        byte[] big = new byte[1024 * 1024 * 8];
        printHeap("大对象分配后");
        sleep(pause);

        // ===== 步骤6：疯狂分配直到 OOM 边缘 =====
        printStep(6, "疯狂分配观察 GC 拼命回收 -> 对象进入老年代");
        try {
            List<byte[]> temp = new ArrayList<>();
            for (int i = 0; i < 100; i++) {
                temp.add(new byte[1024 * 512]); // 每次 512KB
                if (i % 20 == 0) {
                    printHeap("第 " + i + " 次分配后");
                    Thread.sleep(1000);
                }
            }
            temp.clear();
        } catch (OutOfMemoryError e) {
            System.out.println("  !! OutOfMemoryError! 堆不够用了");
        }
        printHeap("最终状态");
        sleep(10);

        System.out.println();
        System.out.println("========== 演示结束 ==========");
        System.out.println();
        System.out.println("回顾 jstat 的输出，你会看到：");
        System.out.println("  YGC 次数增加 = Minor GC");
        System.out.println("  FGC 次数增加 = Full GC");
        System.out.println("  YGCT = Young GC 总耗时");
        System.out.println("  FGCT = Full GC 总耗时");
        System.out.println("  EU/OU = Eden/Old 使用量");
        System.out.println("  S0/S1 = 两个 Survivor 区来回倒");
        System.out.println();
        System.out.println("内存模型总结（面试话术）：");
        System.out.println("  堆 = 新生代(Eden+S0+S1) + 老年代");
        System.out.println("  新生代放新对象，Minor GC 频繁但快");
        System.out.println("  老年代放长期存活对象，Full GC 慢且会 STW");
        System.out.println("  方法区(元空间) 存类信息、静态变量，不在堆里");
        System.out.println("  栈是线程私有的，存局部变量，方法结束就弹出");
        System.out.println();
        System.out.println("进程存活中，你可以继续用 jstat 观察。Ctrl+C 结束。");
        Thread.sleep(600000); // 保持存活 10 分钟
    }

    static void printStep(int n, String desc) {
        System.out.println();
        System.out.println("--- 步骤" + n + "：" + desc + " ---");
    }

    static void printHeap(String label) {
        Runtime rt = Runtime.getRuntime();
        long total = rt.totalMemory() / 1024 / 1024;
        long free = rt.freeMemory() / 1024 / 1024;
        long used = total - free;
        long max = rt.maxMemory() / 1024 / 1024;
        System.out.printf("  [%s] 已用=%dMB / 已申请=%dMB / 最大=%dMB%n",
                label, used, total, max);
    }

    static void sleep(int sec) {
        try {
            System.out.println("  (等待 " + sec + " 秒，去终端看 jstat...)");
            Thread.sleep(sec * 1000L);
        } catch (InterruptedException ignored) {}
    }
}
