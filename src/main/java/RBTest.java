import java.util.Scanner;

/**
 * 红黑树是一种节点带有颜色属性的二叉查找树
 * 红黑树特性:
 * 1.根节点一定是黑色,节点是红色或者黑色
 * 2.所有叶子都是黑色(叶子是NIL节点，即叶子节点为空的节点，无左右子叶的节点)
 * 3.每个红色的节点必须有两个黑色的子节点  (从每个叶子到根的所有路径上不能有两个连续的红色节点)
 *      原因：为了保证树的平衡，保证最长的路径最多是最短路径的的两倍。
 * 4.从任一节点到其每个叶子的所有简单路径都包含相同数目的黑色节点
 * 5.插入结点一定是红色
 *
 * @Author: kx.chen
 * @Date: 2021/12/10 17:59
 */
public class RBTest {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        RBTree<String, Object> rbt = new RBTree<>();
        while (true) {
            System.out.println("请输入key:");
            String key = scanner.next();
            System.out.println();
            rbt.insert(key, null);
            TreeOperation.show(rbt.getRoot());
        }
    }

}
