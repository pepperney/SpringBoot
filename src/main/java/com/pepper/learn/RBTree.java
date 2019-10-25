package com.pepper.learn;

/**
 * 红黑树
 */
public class RBTree {

    public static final boolean RED = false;

    public static final boolean BLACK = true;

    private RBTNode root;//根节点

    private int level;

    class RBTNode{

        private RBTNode parent;//父节点

        private RBTNode left; // 左节点

        private RBTNode right; // 右节点

        private int data; //数据

        private boolean color; // 颜色

        public RBTNode(RBTNode parent,RBTNode left,RBTNode right,int data,boolean color){
            this.parent = parent;
            this.left = left;
            this.right = right;
            this.data = data;
            this.color = color;
        }
    }

    /**
     * 对红黑树左旋 示意图
     *
     *            px               px
     *            /                /
     *           x      左旋       y
     *          / \     =>       / \
     *         lx  y            x  ry
     *            / \          / \
     *           ly  ry       lx  ly
     *
     * @param x
     */
    private void leftRotate(RBTNode x){
        //x节点右子节点
        RBTNode y = x.right;
        //将y的左子节点赋值给x的右子节点
        x.right = y.left;
        //如果y的左子节点非空 将y的左子节点父节点设置为x
        if(null!=y.left){
            y.left.parent = x;
        }
        //将y父节点设置为x的父节点
        y.parent = x.parent;
        //如果x的父节点为空 将根节点设置为y
        if(x.parent==null){
            this.root = y;
        }else{
            if(x.parent.left == x){
                x.parent.left = y; //如果x为左子节点 将y设置为x父节点的左子节点
            }else{
                x.parent.right = y; //如果x为右节点，将y设置为x父节点的右子节点
            }
        }
        //将x设置为y的左子节点
        y.left = x;
        //将y设置为x的父节点
        x.parent = y;
    }

    /**
     * 右旋示意图
     *              py                py
     *              /                 /
     *             y       右旋       x
     *            / \      ==>      / \
     *           x   ry            rx  y
     *          / \                   / \
     *         lx  rx                rx  ry
     * @param y
     */
    private void rightRotate(RBTNode y){
        //y节点左子节点
        RBTNode x = y.left;
        //将x右子节点赋值给y的左子节点
        y.left = x.right;
        //如果x的右子节点非空 将其父节点设置为y
        if(null!=x.right){
            x.right.parent = y;
        }
        //将x的父节点设置为y的父节点
        x.parent = y.parent;
        //如果y的父节点为空 将x设置为根节点
        if(null==y.parent){
            this.root = x;
        }else{
            if(y.parent.right==y){
                y.parent.right = x; //如果y为父节点的右子节点 将x设置为右子节点
            }else{
                y.parent.left = x;  //如果y为父节点的左子节点 将x设置为左子节点
            }
        }
        //将x的右子节点设置为y
        x.right = y;
        //将y的父节点设置x
        y.parent = x;
    }

    private void insert(RBTNode node){
        int cmp;
        RBTNode y = null;
        RBTNode x = this.root;
        //寻找插入在哪个节点后
        while(x!=null){
            y = x;
            if(node.data>x.data){
                x = x.right;
            }else{
                x = x.left;
            }
        }

        //将要插入节点父节点设置为y
        node.parent = y;
        if(null!=y){
            //判断要插入节点是左节点还是右节点
            if(node.data>y.data){
                y.right = node;
            }else{
                y.left = node;
            }
        }else{
            this.root = node;
        }

        //设置节点颜色为红色
        setRed(node);

        //重新调整为红黑树
        insertFixUp(node);
    }


    /**
     * 红黑树插入修正方法
     * @param node
     */
    private void insertFixUp(RBTNode node){
        RBTNode parent,gParent;

        //如果父节点存在，且父节点的颜色是红色
        while((parent = parentOf(node))!=null&&isRed(parent)){
            gParent = parentOf(parent);
            //由于父节点为红色情况，祖父节点必定存在 所以这里不需要判断
            //若父节点为祖父节点的左节点
            if(parent==gParent.left){
                RBTNode uncle = gParent.right;
                //CASE 1: 叔叔节点为红色
                if(uncle!=null&&isRed(uncle)){
                    setBlack(uncle);
                    setBlack(parent);
                    setRed(gParent);
                    node = gParent;
                    continue;
                }
                //CASE 2: 叔叔节点为黑色，且当前节点为右子树
                if(parent.right == node){
                    RBTNode tmp;
                    leftRotate(parent);
                    tmp = parent;
                    parent = node;
                    node = tmp;
                }
                //CASE 3: 叔叔节点是黑色，且当前节点为左子树
                setBlack(parent);
                setRed(gParent);
                rightRotate(gParent);
            }else{
                // Case 1条件：叔叔节点是红色
                RBTNode uncle = gParent.left;
                if ((uncle!=null) && isRed(uncle)) {
                    setBlack(uncle);
                    setBlack(parent);
                    setRed(gParent);
                    node = gParent;
                    continue;
                }
                // Case 2条件：叔叔是黑色，且当前节点是左孩子
                if (parent.left == node) {
                    RBTNode tmp;
                    rightRotate(parent);
                    tmp = parent;
                    parent = node;
                    node = tmp;
                }
                // Case 3条件：叔叔是黑色，且当前节点是右孩子。
                setBlack(parent);
                setRed(gParent);
                leftRotate(gParent);
            }
        }
        setBlack(this.root);
    }


    private void setRed(RBTNode node){
        node.color = RED;
    }

    private RBTNode parentOf(RBTNode node){
        return node.parent;
    }

    private void setBlack(RBTNode node){
        node.color = BLACK;
    }

    private boolean isRed(RBTNode node){
        return !node.color;
    }
}
