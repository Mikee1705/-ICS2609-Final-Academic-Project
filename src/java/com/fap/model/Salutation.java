package com.fap.model;

/**
 * Model: Salutations table (PostgreSQL)
 *
 * Schema:
 *   Salutation_ID  SERIAL  PK
 *   Title          VARCHAR UNIQUE  ('Mr.', 'Ms.', 'Mrs.', 'Dr.', 'Prof.')
 */
public class Salutation {

    private int    salutationId;
    private String title;

    public Salutation() {}

    public Salutation(int salutationId, String title) {
        this.salutationId = salutationId;
        this.title        = title;
    }

    public int getSalutationId()                  { return salutationId; }
    public void setSalutationId(int id)           { this.salutationId = id; }

    public String getTitle()                      { return title; }
    public void setTitle(String s)                { this.title = s; }

    @Override
    public String toString() { return title; }
}
