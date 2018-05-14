package com.ata.region.entity;

import javax.persistence.*;
import java.util.List;
@Entity
public class Region {
    private Long id;
    private Long parentId;
    private Integer level;
    private String type = "";
    private String name;
    private List<Region> children;

    @Id
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @OneToMany(mappedBy="parentId", cascade = CascadeType.ALL, fetch= FetchType.LAZY)
    public List<Region> getChildren() {
        return children;
    }

    public void setChildren(List<Region> children) {
        this.children = children;
    }

    @Override
    public String toString() {
        return "Region{" +
                "id=" + id +
                ", parentId=" + parentId +
                ", level=" + level +
                ", type='" + type + '\'' +
                ", name='" + name + '\'' +
                ", children=" + children +
                '}';
    }
}
