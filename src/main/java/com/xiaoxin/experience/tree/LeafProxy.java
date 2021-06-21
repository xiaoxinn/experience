package com.xiaoxin.experience.tree;

/**
 * @author xiaoxin
 */
public interface LeafProxy<T, E>
{
    E branchMark(T object);
}
