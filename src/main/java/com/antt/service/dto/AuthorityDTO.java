package com.antt.service.dto;

import com.antt.config.Constants;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.Set;

/**
 * Created by antt on 3/16/2018.
 */
public class AuthorityDTO {
    @NotBlank
    @Pattern(regexp = Constants.GROUP_REGEX)
    @Size(min = 1, max = 50)
    private String name;

    private boolean enabled = true;

    Set<String> rights;

    private String parent;

    @Override
    public String toString() {
        return "AuthorityDTO{" +
            "name='" + name + '\'' +
            ", enabled=" + enabled +
            ", rights=" + rights +
            '}';
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public Set<String> getRights() {
        return rights;
    }

    public void setRights(Set<String> rights) {
        this.rights = rights;
    }

    public String getParent() {
        return parent;
    }

    public void setParent(String parent) {
        this.parent = parent;
    }
}
