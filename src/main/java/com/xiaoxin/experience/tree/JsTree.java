package com.xiaoxin.experience.tree;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author xiaoxin
 */
public class JsTree implements Serializable,Treeable<JsTree>
{
    private static final long serialVersionUID = -1729942394944545519L;

    private String id;

    private String parentId;

    private String label;

    private String type;

    private Object payload;

    private boolean isLeaf;

    private State state;

    private List<JsTree> children;

    public static JsTree asLeaf(String id, String label, String type, Object payload)
    {
        JsTree jsTree = new JsTree(id, label, type, payload);
        jsTree.setLeaf(true);
        return jsTree;
    }

    public static JsTree asBranch(String id, String label, String type, Object payload)
    {
        return new JsTree(id, label, type, payload);
    }

    public JsTree()
    {

    }

    public JsTree(String id, String parentId, String label, String type)
    {
        this.id = id;
        this.parentId = parentId;
        this.label = label;
        this.type = type;
    }

    public JsTree(String id, String label, String type, Object payload)
    {
        this.id = id;
        this.label = label;
        this.type = type;
        this.payload = payload;
    }

    public JsTree(String id, String parentId, String label, String type, Object payload)
    {
        this.id = id;
        this.parentId = parentId;
        this.label = label;
        this.type = type;
        this.payload = payload;
    }

    @Override
    public void addChild(JsTree jsTree)
    {
        if (null == jsTree)
        {
            return;
        }

        if (null == children)
        {
            children = new ArrayList<>();
        }
        children.add(jsTree);
    }

    public State newState()
    {
        this.state = new State();
        return state;
    }

    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public String getParentId()
    {
        return parentId;
    }

    public void setParentId(String parentId)
    {
        this.parentId = parentId;
    }

    public String getLabel()
    {
        return label;
    }

    public void setLabel(String label)
    {
        this.label = label;
    }

    public String getType()
    {
        return type;
    }

    public void setType(String type)
    {
        this.type = type;
    }

    public Object getPayload()
    {
        return payload;
    }

    public void setPayload(Object payload)
    {
        this.payload = payload;
    }

    public State getState()
    {
        return state;
    }

    public void setState(State state)
    {
        this.state = state;
    }

    public boolean isLeaf()
    {
        return isLeaf;
    }

    public void setLeaf(boolean leaf)
    {
        isLeaf = leaf;
    }

    public List<JsTree> getChildren()
    {
        return children;
    }

    public void setChildren(List<JsTree> children)
    {
        this.children = children;
    }

    public static class State implements Serializable
    {
        private static final long serialVersionUID = -658404076169153567L;

        private int opened;

        private int selected;

        private int checked;

        private int disabled;

        public State()
        {
            this(0, 0, 0);
        }

        public State(int opened, int selected, int disabled)
        {
            this(opened, selected, selected, disabled);
        }

        public State(int opened, int selected, int checked, int disabled)
        {
            this.opened = opened;
            this.selected = selected;
            this.checked = checked;
            this.disabled = disabled;
        }

        public State open()
        {
            this.opened = 1;
            return this;
        }


        public State select()
        {
            this.selected = 1;
            return this;
        }

        public State check()
        {
            this.checked = 1;
            return this;
        }

        public State disable()
        {
            this.disabled = 1;
            return this;
        }

        public int getOpened()
        {
            return opened;
        }

        public void setOpened(int opened)
        {
            this.opened = opened;
        }

        public int getSelected()
        {
            return selected;
        }

        public void setSelected(int selected)
        {
            this.selected = selected;
        }

        public int getChecked()
        {
            return checked;
        }

        public void setChecked(int checked)
        {
            this.checked = checked;
        }

        public int getDisabled()
        {
            return disabled;
        }

        public void setDisabled(int disabled)
        {
            this.disabled = disabled;
        }

    }
}


