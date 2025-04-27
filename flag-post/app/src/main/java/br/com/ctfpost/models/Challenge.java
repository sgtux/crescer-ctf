package br.com.ctfpost.models;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "challenge", schema = "flagpost")
public class Challenge {

    @Id
    public int id;

    public String title;

    public String description;

    public int score;

    public String site;
}
