package com.xiaoxin.experience.tree;

import java.util.*;

/**
 * @author Beowulf
 */
public class AnyTree<T, V, E>
{
    private T backRoot;

    private Branch<T, V, E> treeRoot;

    private List<Branch<T, V, E>> branches = new ArrayList<>();

    private List<T> flatNodeList = new ArrayList<>();

    private boolean markChanged = true;

    private BranchProxy<T, E> branchProxy;

    private LeafProxy<V, E> leafProxy;

    public AnyTree(BranchProxy<T, E> branchProxy, LeafProxy<V, E> leafProxy)
    {
        this.branchProxy = branchProxy;
        this.leafProxy = leafProxy;
    }

    public AnyTree<T, V, E> createTree(Collection<? extends T> flatTree)
    {
        return createTree(flatTree, null);
    }

    public AnyTree<T, V, E> createTree(Collection<? extends T> flatTree, T backRoot)
    {
        return createTree(flatTree, backRoot, false);
    }

    public AnyTree<T, V, E> createTree(Collection<? extends T> flatTree, T backRoot, boolean forceRoot)
    {
        this.backRoot = backRoot;
        if (flatTree != null && !flatTree.isEmpty())
        {
            //高性能实现，将传入的数组映射成map结构
            Map<E, T> nodeMap = new HashMap<>(16);
            Map<E, List<T>> nodeGroupMap = new HashMap<>(16);
            for (T item : flatTree)
            {
                nodeMap.put(branchProxy.branchMark(item), item);
                List<T> children = nodeGroupMap.computeIfAbsent(branchProxy.branchFrom(item), k -> new ArrayList<>());
                children.add(item);
            }

            Set<E> nodeIdSet = nodeMap.keySet();
            List<T> foundRoots = new ArrayList<>();
            Map<E, Branch<T, V, E>> branchMap = new HashMap<>(16);

            for (E groupId : nodeGroupMap.keySet())
            {
                if (nodeIdSet.contains(groupId))
                {
                    T groupItem = nodeMap.get(groupId);
                    branchMap.put(groupId, new Branch<>(branchProxy, groupItem));
                }
                else
                {
                    //将父组不存在的分组取出作为根节点处理
                    foundRoots.addAll(nodeGroupMap.get(groupId));
                }
            }

            T rootNode = handleFilterRoots(foundRoots, forceRoot);
            treeRoot = new Branch<>(branchProxy, rootNode);
            branchMap.put(branchProxy.branchMark(rootNode), treeRoot);

            for (Map.Entry<E, List<T>> entry : nodeGroupMap.entrySet())
            {
                //获取组信息的树枝对象，由上一个循环看出组不存的情况下，都放到根组下了
                Branch<T, V, E> branch = branchMap.get(entry.getKey());
                if (null == branch)
                {
                    branch = treeRoot;
                }

                for (T t : entry.getValue())
                {
                    Branch<T, V, E> child = branchMap.get(branchProxy.branchMark(t));
                    if (null == child)
                    {
                        child = new Branch<>(branchProxy, t);
                    }
                    //上面的branch = treeRoot;赋值会可能导致child就是root,因为handleFilterRoots可能返回仅一个根节点的树
                    if (branch != child)
                    {
                        branch.addChild(child);
                    }
                }
            }

            //嵌套实现，简单方便，但是性能低下
            //treeRoot = addTreeNode(flatTree, findRoot(flatTree, forceRoot));
        }
        else
        {
            treeRoot = new Branch<>(branchProxy, backRoot);
        }

        markChange();
        reloadBranches();
        return this;
    }

    public AnyTree<T, V, E> bindLeaves(Set<E> branchIdSet)
    {
        branches.stream()
                .filter(branch -> branchIdSet.contains(branchProxy.branchMark(branch.getPayload())))
                .forEach(Branch::setHasLeaf);
        return this;
    }

    public AnyTree<T, V, E> decorateLeaves(Collection<? extends V> leaves)
    {
        if (null == leafProxy)
        {
            throw new IllegalStateException("leaf proxy not config.");
        }

        Map<E, List<V>> leafMap = new HashMap<>(16);
        for (V leaf : leaves)
        {
            List<V> branchLeaves = leafMap.computeIfAbsent(leafProxy.branchMark(leaf), k -> new ArrayList<>());
            branchLeaves.add(leaf);
        }

        for (Branch<T, V, E> branch : branches)
        {
            E branchMark = branchProxy.branchMark(branch.getPayload());
            branch.setLeaves(leafMap.remove(branchMark));
        }

        for (Map.Entry<E, List<V>> entry : leafMap.entrySet())
        {
            treeRoot.addLeaves(entry.getValue());
        }
        return this;
    }

    public AnyTree<T, V, E> simplifyRoot()
    {
        while (treeRoot.childrenCount() == 1)
        {
            Branch<T, V, E> nextRoot = treeRoot.getChildAt(0);
            treeRoot = nextRoot.makeRoot();
        }
        markChange();
        return this;
    }

    public AnyTree<T, V, E> cutNakedBranches()
    {
        treeRoot.cutNakedBranches();
        markChange();
        return this;
    }

    public List<Branch<T, V, E>> getBranchesUnderDepth(int depth)
    {
        List<Branch<T, V, E>> list = new ArrayList<>();
        for (Branch<T, V, E> branch : branches)
        {
            if (branch.getDepth() > depth)
            {
                list.add(branch);
            }
        }
        return list;
    }

    public Branch<T, V, E> getTreeRoot()
    {
        reloadBranches();
        return treeRoot;
    }

    public List<T> getFlatNodeList()
    {
        reloadBranches();
        return flatNodeList;
    }

    public List<Branch<T, V, E>> getBranches()
    {
        return new ArrayList<>(branches);
    }

    public Branch<T, V, E> addBranch(Branch<T, V, E> parent, T child)
    {
        Branch<T, V, E> branch = new Branch<>(branchProxy, child);
        parent.addChild(branch);
        return branch;
    }

    private void markChange()
    {
        this.markChanged = true;
    }

    private void reloadBranches()
    {
        if (markChanged)
        {
            markChanged = false;
            branches.clear();
            if (null == treeRoot)
            {
                treeRoot = new Branch<>(branchProxy, backRoot);
            }
            else
            {
                treeRoot.makeRoot().collectAllChildren(branches);
            }
            flatNodeList.clear();
            branches.sort(Comparator.comparing(Branch::getDepth));
            for (Branch<T, V, E> branch : branches)
            {
                flatNodeList.add(branch.getPayload());
            }
        }
    }

    private T handleFilterRoots(List<T> foundRoots, boolean forceRoot)
    {
        if (foundRoots.isEmpty())
        {
            throw new IllegalStateException("there is no root found in this tree.");
        }

        if (!forceRoot && foundRoots.size() == 1)
        {
            return foundRoots.get(0);
        }

        if (null == backRoot)
        {
            throw new IllegalStateException("there is more than one root found in this tree.");
        }

        for (T found : foundRoots)
        {
            branchProxy.branchReset(found, branchProxy.branchMark(backRoot));
        }
        return backRoot;
    }

    private boolean match(T asChild, T asParent)
    {
        E a = branchProxy.branchFrom(asChild);
        E b = branchProxy.branchMark(asParent);
        return Objects.equals(a, b);
    }
}

