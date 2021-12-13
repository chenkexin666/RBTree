import lombok.Data;

/**
 * @Author: kx.chen
 * @Date: 2021/12/13 10:08
 */
@Data
public class RBNode<Key extends Comparable<Key>, Value> {

    public Key key;

    public Value value;

    public RBNode<Key, Value> left;

    public RBNode<Key, Value> right;

    public RBNode<Key, Value> parent;

    public boolean color;

}
