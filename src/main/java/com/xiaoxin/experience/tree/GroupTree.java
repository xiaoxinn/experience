package com.xiaoxin.experience.tree;

import java.util.List;


/**
 * @author xiaoxin
 */
public class GroupTree extends AnyTree<Group, Device,Integer> {


    public GroupTree(List<Group> list)
    {
        super(new OrganizationInfoProxy(),new TollgateProxy());
        createTree(list);
    }

    public GroupTree(List<Group> list, Group backRoot)
    {
        super(new OrganizationInfoProxy(), new TollgateProxy());
        createTree(list, backRoot);
    }

    public GroupTree(List<Group> list, Group backRoot, boolean forceRoot)
    {
        super(new OrganizationInfoProxy(), new TollgateProxy());
        createTree(list, backRoot, forceRoot);
    }

    private static class OrganizationInfoProxy implements BranchProxy<Group, Integer>
    {
        @Override
        public Integer branchMark(Group object)
        {
            return object.getId();
        }

        @Override
        public Integer branchFrom(Group object)
        {
            return object.getParentId();
        }

        @Override
        public void branchReset(Group object, Integer parent)
        {
            object.setParentId(null == parent ? -1 : parent);
        }
    }


    private static class TollgateProxy implements LeafProxy<Device, Integer>
    {
        @Override
        public Integer branchMark(Device object)
        {
            return object.getOrganizationId();
        }
    }

}

