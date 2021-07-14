package com.xiaoxin.experience.tree;

/**
 * @author xiaoxin
 */
public interface BranchProxy<T, E>
{
    E branchMark(T object);

    E branchFrom(T object);

    void branchReset(T object, E parent);
}
