/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cercetare.std.recomandation;

import java.util.Date;
import java.util.Objects;

/**
 *
 * @author ciprian
 */
public class Page {

    private Integer categoryId;
    private String category;
    private Date date;

    public Page(String category, Date date) {
        this.category = category;
        this.date = date;
    }

    public Page(Integer categoryId, String category, Date date) {
        this.categoryId = categoryId;
        this.category = category;
        this.date = date;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof Page)) {
            return false;
        }
        Page other = (Page) obj;
        return this.getCategory().equals(other.getCategory());

    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 79 * hash + Objects.hashCode(this.getCategory());
        return hash;
    }

    /**
     * @return the category
     */
    public String getCategory() {
        return category;
    }

    /**
     * @return the date
     */
    public Date getDate() {
        return date;
    }

    public Integer getCategoryId() {
        return categoryId;
    }

}
