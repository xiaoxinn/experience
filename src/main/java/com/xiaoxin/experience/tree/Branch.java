package com.xiaoxin.experience.tree;

import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.function.Function;

public class Branch<T, V, E>
{
    private Branch<T, V, E> parent;

    private List<Branch<T, V, E>> children = new ArrayList<>();

    private List<V> leaves = null;

    private int leafCount = 0;

    private int depth = 0;

    private T payload;

    private BranchProxy<T, E> branchProxy;

    Branch(BranchProxy<T, E> branchProxy, T payload)
    {
        this.branchProxy = branchProxy;
        this.payload = payload;
    }

    public boolean isRoot()
    {
        return parent == null;
    }

    public Branch<T, V, E> makeRoot()
    {
        this.parent = null;
        this.branchProxy.branchReset(payload, null);
        return this;
    }

    public void addChild(Branch<T, V, E> childNode)
    {
        if (childNode != null)
        {
            childNode.parent = this;
            childNode.reCountDepth();
            children.add(childNode);
        }
    }

    private void reCountDepth()
    {
        if (!isRoot())
        {
            this.depth = parent.depth + 1;
        }
        for (Branch<T, V, E> child : children)
        {
            child.reCountDepth();
        }
    }

    public void collectAllChildrenLeaves(Collection<V> childrenLeaves)
    {
        if (!children.isEmpty())
        {
            for (Branch<T, V, E> child : children)
            {
                child.collectAllChildrenLeaves(childrenLeaves);
            }
        }
        if (!CollectionUtils.isEmpty(leaves))
        {
            childrenLeaves.addAll(leaves);
        }
    }

    public void collectAllSelectBranch(Collection<E> childrenBranchMarks, Set<E> selects)
    {
        if (selects.contains(branchProxy.branchMark(payload)))
        {
            Collection<E> list = new ArrayList<>();
            collectAllChildrenBranchMark(list);
            childrenBranchMarks.addAll(list);
        }
        else if (!children.isEmpty())
        {
            for (Branch<T, V, E> child : children)
            {
                child.collectAllSelectBranch(childrenBranchMarks, selects);
            }
        }
    }

    public void collectAllChildrenBranchMark(Collection<E> childrenBranchMarks)
    {
        if (!children.isEmpty())
        {
            for (Branch<T, V, E> child : children)
            {
                child.collectAllChildrenBranchMark(childrenBranchMarks);
            }
        }
        childrenBranchMarks.add(branchProxy.branchMark(payload));
    }

    public void collectAllChildrenPayloads(Collection<T> childrenPayloads)
    {
        if (!children.isEmpty())
        {
            for (Branch<T, V, E> child : children)
            {
                child.collectAllChildrenPayloads(childrenPayloads);
            }
        }
        childrenPayloads.add(payload);
    }

    public void collectAllChildren(Collection<Branch<T, V, E>> childrenCollection)
    {
        if (!children.isEmpty())
        {
            for (Branch<T, V, E> child : children)
            {
                child.collectAllChildren(childrenCollection);
            }
        }
        childrenCollection.add(this);
    }

    public int countTotalLeaves()
    {
        int leavesCount = 0;
        if (!children.isEmpty())
        {
            for (Branch<T, V, E> child : children)
            {
                leavesCount += child.countTotalLeaves();
            }
        }
        leavesCount += this.leafCount;
        return leavesCount;
    }

    public void cutNakedBranches()
    {
        if (!isRoot() && countTotalLeaves() == 0)
        {
            parent.children.remove(this);
        }
        else
        {
            for (Branch<T, V, E> child : getChildrenArray())
            {
                child.cutNakedBranches();
            }
        }
    }

    public void cutIfNaked()
    {
        if (!isRoot() && isNaked())
        {
            parent.children.remove(this);
        }
    }

    public boolean isNaked()
    {
        return (childrenCount() + getLeafCount()) == 0;
    }

    public int childrenCount()
    {
        return children.size();
    }

    public Branch<T, V, E> getChildAt(int index)
    {
        return children.get(index);
    }

    public Branch<T, V, E> getChildOf(T payload)
    {
        for (Branch<T, V, E> child : children)
        {
            if (child.payload == payload)
            {
                return child;
            }
        }
        return null;
    }

    public List<Branch<T, V, E>> getChildrenArray()
    {
        return new ArrayList<>(children);
    }

    public List<V> getAllChildrenLeaves()
    {
        List<V> leaves = new ArrayList<>();
        collectAllChildrenLeaves(leaves);
        return leaves;
    }

    public List<T> getAllChildrenPayloads()
    {
        List<T> payloads = new ArrayList<>();
        collectAllChildrenPayloads(payloads);
        return payloads;
    }

    public List<T> getChildrenPayloads()
    {
        List<T> payloads = new ArrayList<>(childrenCount());
        for (Branch<T, V, E> child : children)
        {
            payloads.add(child.payload);
        }
        return payloads;
    }

    public Branch<T, V, E> findByMark(E mark)
    {
        if (null == mark)
        {
            return null;
        }

        if (mark.equals(branchProxy.branchMark(payload)))
        {
            return this;
        }

        for (Branch<T, V, E> child : children)
        {
            Branch<T, V, E> branch = child.findByMark(mark);
            if (null != branch)
            {
                return branch;
            }
        }
        return null;
    }

    public Branch<T, V, E> findParentOf(T payload)
    {
        if (this.payload == payload)
        {
            return this;
        }
        else if (!isRoot())
        {
            return parent.findParentOf(payload);
        }
        else
        {
            return null;
        }
    }

    public List<Branch<T, V, E>> getAllParents()
    {
        Stack<Branch<T, V, E>> branchStack = new Stack<>();
        Branch<T, V, E> nowBranch = this;
        while (!nowBranch.isRoot())
        {
            nowBranch = nowBranch.parent;
            branchStack.push(nowBranch);
        }

        List<Branch<T, V, E>> list = new ArrayList<>();
        while (!branchStack.isEmpty())
        {
            list.add(branchStack.pop());
        }
        return list;
    }

    public List<T> getAllParentPayloads()
    {
        Stack<T> branchStack = new Stack<>();
        Branch<T, V, E> nowBranch = this;
        while (!nowBranch.isRoot())
        {
            nowBranch = nowBranch.parent;
            branchStack.push(nowBranch.getPayload());
        }

        List<T> list = new ArrayList<>();
        while (!branchStack.isEmpty())
        {
            list.add(branchStack.pop());
        }
        return list;
    }

    public void setHasLeaf()
    {
        setLeafCount(1);
    }

    public void addLeaf(V leaf)
    {
        if (null == leaves)
        {
            leaves = new ArrayList<>();
        }
        leaves.add(leaf);
        setLeafCount(leaves.size());
    }

    public void removeLeaf(V leaf)
    {
        if (null != leaves)
        {
            leaves.remove(leaf);
            setLeafCount(leaves.size());
        }
    }

    public void setLeaves(List<V> leaves)
    {
        this.leaves = leaves;
        setLeafCount(null == leaves ? 0 : leaves.size());
    }

    public void addLeaves(List<V> leaves)
    {
        if (null == this.leaves)
        {
            setLeaves(leaves);
        }
        else
        {
            this.leaves.addAll(leaves);
            setLeafCount(this.leaves.size());
        }
    }

    public int getLeafCount()
    {
        return leafCount;
    }

    public void setLeafCount(int leafCount)
    {
        this.leafCount = leafCount;
    }

    public V getLeafAt(int index)
    {
        if (null == leaves || index >= leafCount)
        {
            return null;
        }
        return leaves.get(index);
    }

    public Branch<T, V, E> getParent()
    {
        return parent;
    }

    public int getDepth()
    {
        return depth;
    }

    public T getPayload()
    {
        return payload;
    }

    public JsTree buildJsTree(Function<T, JsTree> branchBuilder)
    {
        return buildTree(branchBuilder);
    }

    public JsTree buildJsTree(Function<T, JsTree> branchBuilder, Function<V, JsTree> leafBuilder)
    {
        return buildTree(branchBuilder, leafBuilder);
    }

    public <Tree extends Treeable<Tree>> Tree buildTree(Function<T, Tree> branchBuilder)
    {
        return buildTree(branchBuilder, null);
    }

    public <Tree extends Treeable<Tree>> Tree buildTree(Function<T, Tree> branchBuilder, Function<V, Tree> leafBuilder)
    {
        Tree jsTree = branchBuilder.apply(payload);
        for (Branch<T, V, E> child : children)
        {
            jsTree.addChild(child.buildTree(branchBuilder, leafBuilder));
        }
        if (null != leafBuilder && !CollectionUtils.isEmpty(leaves))
        {
            for (V leaf : leaves)
            {
                jsTree.addChild(leafBuilder.apply(leaf));
            }
        }
        return jsTree;
    }
}
