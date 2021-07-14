package com.xiaoxin.experience.tree;

/**
 * @author xiaoxin
 */
public class Group {

        private Integer id;

        private String name;

        private Integer parentId;

        private String parents;

    public Group() {
    }

    public Group(Integer id, Integer parentId, String parents, String name) {
            this.id = id;
            this.parentId = parentId;
            this.parents = parents;
            this.name = name;
        }

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public Integer getParentId() {
            return parentId;
        }

        public void setParentId(Integer parentId) {
            this.parentId = parentId;
        }

        public String getParents() {
            return parents;
        }

        public void setParents(String parents) {
            this.parents = parents;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public JsTree toTreeNode(Group group) {
            return JsTree.asBranch(String.valueOf(getId()), getName(), "1", group);
        }

}
