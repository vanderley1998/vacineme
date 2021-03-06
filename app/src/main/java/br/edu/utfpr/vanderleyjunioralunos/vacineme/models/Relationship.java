package br.edu.utfpr.vanderleyjunioralunos.vacineme.models;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;

import br.edu.utfpr.vanderleyjunioralunos.vacineme.R;

public class Relationship{

    private String description;
    private Drawable icon;

    public Relationship(String description) {
        this.description = description;
    }

    public Relationship(String description, Drawable icon) {
        this.description = description;
        this.icon = icon;
    }

    public static int findRelationshipPosition(String description, Context context){
        String[] rs = context.getResources().getStringArray(R.array.relationship);
        for(int i=0;i<rs.length;i++){
            if(rs[i].equalsIgnoreCase(description)){
                return i;
            }
        }
        return -1;
    }

    public static Drawable findIcon(int relationship, Context context) {
        TypedArray icons = context.getResources().obtainTypedArray(R.array.icones_parentesco);
        return icons.getDrawable(relationship);
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Drawable getIcon() {
        return icon;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }

}
