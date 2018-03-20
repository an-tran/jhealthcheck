package com.antt.domain;

import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * An authority (a security role) used by Spring Security.
 */
@Entity
@Table(name = "jhi_authority")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Authority implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull
    @Size(max = 50, message = "Group name is allowed maximum 50 characters")
    @Id
    @Column(length = 50)
    private String name;

//    @JsonIgnore
    @ManyToMany
    @JoinTable(
        name = "jhi_authority_right",
        joinColumns = {@JoinColumn(name = "authority_name", referencedColumnName = "name")},
        inverseJoinColumns = {@JoinColumn(name = "right_name", referencedColumnName = "name")})
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    @BatchSize(size = 20)
    Set<Right> rights = new HashSet<>();

//    @NotNull
    @Column(nullable = false)
    private boolean enabled = true;

    @ManyToOne(optional = false)
    @JoinColumn(name="created_by", nullable = false)
    private User creator;

    @ManyToOne(optional = false)
    @JoinColumn(name="owner", nullable = false)
    private User owner;


    @OneToOne
    private Authority parent;

    public User getCreator() {
        return creator;
    }

    public void setCreator(User creator) {
        this.creator = creator;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<Right> getRights() {
        return rights;
    }

    public void setRights(Set<Right> rights) {
        this.rights = rights;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Authority authority = (Authority) o;

        return !(name != null ? !name.equals(authority.name) : authority.name != null);
    }

    @Override
    public int hashCode() {
        return name != null ? name.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "Authority{" +
            "name='" + name + '\'' +
            "enabled='" + enabled+ '\'' +
            "}";
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public Authority getParent() {
        return parent;
    }

    public void setParent(Authority parent) {
        this.parent = parent;
    }
}
