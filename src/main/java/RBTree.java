import lombok.Data;

/**
 * @Author: kx.chen
 * @Date: 2021/12/10 17:13
 */
@Data
public class RBTree<Key extends Comparable<Key>, Value> {

    private static final boolean RED = true;
    private static final boolean BLACK = false;

    // 树根的引用
    private RBNode<Key, Value> root;


    /**
     * 获取当前节点的父节点
     */
    private RBNode<Key, Value> parentOf(RBNode<Key, Value> node) {
        if (null == node) {
            return null;
        }
        return node.parent;
    }

    /**
     * 判断当前节点是否是红色
     */
    private boolean isRed(RBNode<Key, Value> node) {
        if (null == node) {
            return false;
        }
        return node.color == RED;
    }

    /**
     * 判断当前节点是否是黑色
     */
    private boolean isBlack(RBNode<Key, Value> node) {
        if (null == node) {
            return false;
        }
        return node.color == BLACK;
    }


    /**
     * 设置当前节点为红色
     */
    private void setRed(RBNode<Key, Value> node) {
        if (null != node) {
            node.color = RED;
        }
    }

    /**
     * 设置当前节点为黑色
     */
    private void setBlack(RBNode<Key, Value> node) {
        if (null != node) {
            node.color = BLACK;
        }
    }


    /**
     * 中序打印二叉树
     */
    public void inOrderPrint() {
        inOrderPrint(this.root);
    }

    private void inOrderPrint(RBNode<Key, Value> node) {
        if (null != node) {
            inOrderPrint(node.left);
            System.out.println("key:" + node.key + ",value:" + node.value);
            inOrderPrint(node.right);
        }
    }

    public void insert(Key key, Value value) {
        RBNode<Key, Value> node = new RBNode<>();
        node.setKey(key);
        node.setValue(value);
        // 新节点一定是红色
        node.setColor(RED);

        insert(node);
    }

    private void insert(RBNode<Key, Value> node) {
        RBNode<Key, Value> parent = null;
        RBNode<Key, Value> x = this.root;

        while (x != null) {
            parent = x;
            // cmp > 0 说明，node.key 大于x.key, 需要到x的右子树查找
            // cmp == 0 说明 node.key 等于 x.key 说明需要进行替换操作
            // cmp < 0 说明，node.key 小于 x.key, 需要到x的左子树查找
            int cmp = node.key.compareTo(x.key);
            if (cmp > 0) {
                x = x.right;
            } else if ((cmp == 0)) {
                x.setValue(node.value);
                return;
            } else {
                x = x.left;
            }
        }

        node.parent = parent;
        if (null != parent) {
            // 判断node 与 parent 的 key谁大
            int cmp = node.key.compareTo(parent.key);
            if (cmp > 0) { // 说明当前 nodo的key大于 parent的key,需要将node放入parent的右子节点
                parent.right = node;
            } else { // 说明当前 nodo的key 小于 parent的key,需要将node放入parent的左子节点
                parent.left = node;
            }
        } else {
            this.root = node;

        }
        // 需要调用修复红黑树平衡的方法
        insertFixUp(node);
    }

    /**
     * 插入后修复红黑树平衡的方法
     * <p>
     * 说明：
     * |---情景1：红黑树为空树，将根节点染色为黑色
     * |---情景2：插入节点的key已经存在，不需要处理
     * |---情景3：插入节点的父节点为黑色，因为你所插入的路径，黑色节点没有变化，所有红黑树依然平衡，所以不需要处理
     * <p>
     * 情景4：需要我们去处理
     * |---情景4： 插入节点父节点为红色
     * |---情景4.1：叔叔节点存在，并且为红色（父-叔 双红），
     * 将爸爸和叔叔节点染色为黑色，将爷爷节点染色为红色，并且在以爷爷节点为当前节点，进行下一轮处理
     * |---情景4.2：叔叔节点不存在，或者为黑色，父节点为爷爷节点的左子树
     * |---情景4.2.1：插入节点为其父节点的左子节点（LL情况），
     * 将爸爸染色为黑色，将爷爷染色为红色，然后以爷爷节点右旋
     * |---情景4.2.2：插入节点为其父节点的右子节点（LR情况），
     * 以爸爸节点进行一次左旋，得到LL双红的情景（4.2.1）,然后再以爸爸节点为当前节点进行下一轮处理
     * |---情景4.3：叔叔节点不存在，或者为黑色，父节点为爷爷节点的右子树
     * |---情景4.3.1：插入节点为其父节点的右子节点（RR情况），
     * 将爸爸染色为黑色，将爷爷染色为红色，然后以爷爷节点左旋
     * |---情景4.3.2：插入节点为其父节点的左子节点（RL情况），
     * 以爸爸节点进行一次右旋，得到RR双红的情景（4.3.1），然后指定爸爸节点为当前节点进行下一轮处理
     */
    private void insertFixUp(RBNode<Key, Value> node) {
        this.root.setColor(BLACK);

        RBNode<Key, Value> parent = parentOf(node);   // 得到当前节点的父节点
        RBNode<Key, Value> gParent = parentOf(parent); // 得到当前节点的爷爷节点

        // 情景4：插入节点父节点为红色
        if (isRed(parent)) {
            // 如果父节点是红色，那么一定存在爷爷节点，因为跟节点不可能是红色
            RBNode<Key, Value> uncle;

            // 判断父节点是不是在爷爷节点的左边
            if (parent == gParent.left) {
                uncle = gParent.right;

                // 情景4.1：叔叔节点存在，并且为红色（父-叔 双红）
                if (isRed(uncle)) {
                    //  将爸爸和叔叔节点染色为黑色，将爷爷节点染色为红色，并且在以爷爷节点为当前节点，进行下一轮处理
                    setBlack(parent);
                    setBlack(uncle);
                    setRed(gParent);
                    insertFixUp(gParent);
                    return;
                }
                // 情景4.2：叔叔节点不存在，或者为黑色，父节点为爷爷节点的左子树
                if (null == uncle || isBlack(uncle)) {
                    // 情景4.2.1：插入节点为其父节点的左子节点（LL情况），将爸爸染色为黑色，将爷爷染色为红色，然后以爷爷节点右旋
                    if (node == parent.left) {
                        setBlack(parent);
                        setRed(gParent);
                        rightRotate(gParent);
                        return;
                    }
                    // 情景4.2.2：插入节点为其父节点的右子节点（LR情况），
                    // 以爸爸节点进行一次左旋，得到LL双红的情景（4.2.1）,然后再以爸爸节点为当前节点进行下一轮处理
                    if (node == parent.right) {
                        leftRotate(parent);
                        insertFixUp(parent);
                    }
                }
            } else { //父节点为爷爷节点的右子树
                uncle = gParent.left;
                // 情景4.1：叔叔节点存在，并且为红色（父-叔 双红）
                if (isRed(uncle)) {
                    //  将爸爸和叔叔节点染色为黑色，将爷爷节点染色为红色，并且在以爷爷节点为当前节点，进行下一轮处理
                    setBlack(parent);
                    setBlack(uncle);
                    setRed(gParent);
                    insertFixUp(gParent);
                    return;
                }

                // 情景4.3：叔叔节点不存在，或者为黑色，父节点为爷爷节点的右子树
                if (null == uncle || isBlack(uncle)) {
                    // 情景4.3.1：插入节点为其父节点的右子节点（RR情况）
                    // 将爸爸染色为黑色，将爷爷染色为红色，然后以爷爷节点左旋
                    if (node == parent.right) {
                        setBlack(parent);
                        setRed(gParent);
                        leftRotate(gParent);
                        return;
                    }

                    // 情景4.3.2：插入节点为其父节点的左子节点（RL情况）
                    // 以爸爸节点进行一次右旋，得到RR双红的情景（4.3.1），然后指定爸爸节点为当前节点进行下一轮处理
                    if (node == parent.left) {
                        rightRotate(parent);
                        insertFixUp(parent);
                    }
                }
            }
        }
    }


    /**
     * 左旋方法
     * <p>
     * 左旋示意图：左旋x节点
     * <p>
     * p                      p
     * |                      |
     * x                      y
     * / \      ----->        / \
     * lx   y                  x   ry
     * / \                / \
     * ly   ry            lx   ly
     * <p>
     * 1. 将y的左子节点的父节点更新为x, 并将x的右子节点指向y的左子节点（ly）
     * 2. 将x的父节点不为空时，更新y的父节点为x的父节点，并将x的父节点 指定 子树 （当前x的子树位置） 指定为y
     * 3. 将x的父节点 更新为y,将y的左子节点更新为x
     */
    private void leftRotate(RBNode<Key, Value> x) {
        RBNode<Key, Value> y = x.right;
        // 1. 将x的右子节点指向y的左子节点（ly）
        x.right = y.left;
        // 1.1 如果y的左子节点不为null,将y的左子节点的父节点更新为x
        if (null != y.left) {
            y.left.parent = x;
        }

        // 2. 将x的父节点不为空时，更新y的父节点为x的父节点，并将x的父节点 指定 子树 （当前x的子树位置） 指定为y
        if (null != x.parent) {
            y.parent = x.parent;
            // x在x父节点的左边
            if (x == x.parent.left) {
                x.parent.left = y;
            } else {
                x.parent.right = y;
            }
        } else {
            // 说明当前x为根节点，需要更新y的新的根节点
            this.root = y;
            this.root.parent = null;
        }

        // 3. 将x的父节点 更新为y,将y的左子节点更新为x
        x.parent = y;
        y.left = x;
    }

    /**
     * 右旋方法
     * <p>
     * 右旋示意图：右旋y节点
     * <p>
     * p                  p
     * |                  |
     * y                  x
     * / \     ----->     / \
     * x   ry            lx   y
     * / \                    / \
     * lx   ly                ly   ry
     * <p>
     * 1. 将y的左子节点指向x的右子节点, 并且更新x的右子节点的父节点为y
     * 2. 将y的父节点不为空时，更新x的父节点为y的父节点，并将y的父节点 指定 子树 （当前y的子树位置） 指定为x
     * 3. 将y的父节点 更新为x,将x的右子节点更新为y
     */
    private void rightRotate(RBNode<Key, Value> y) {
        RBNode<Key, Value> x = y.left;
        // 1. 将y的左子节点指向x的右子节点, 并且更新x的右子节点的父节点为y
        y.left = x.right;
        if (null != x.right) {
            x.right.parent = y;
        }

        // 2. 将y的父节点不为空时，更新x的父节点为y的父节点，并将y的父节点 指定 子树 （当前y的子树位置） 指定为x
        if (null != y.parent) {
            x.parent = y.parent;
            if (y == y.parent.left) {
                y.parent.left = x;
            } else {
                y.parent.right = x;
            }
        } else {
            // 说明当前y为根节点，需要更新x的新的根节点
            this.root = x;
            this.root.parent = null;
        }

        // 3. 将y的父节点 更新为x,将x的右子节点更新为y
        y.parent = x;
        x.right = y;
    }
}
