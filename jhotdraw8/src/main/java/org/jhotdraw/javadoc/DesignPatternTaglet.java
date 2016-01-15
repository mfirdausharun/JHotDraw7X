/* @(#)DesignPatternTaglet.java
 * Copyright (c) 2016 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw.javadoc;

import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.Doc;
import com.sun.javadoc.SeeTag;
import com.sun.javadoc.SourcePosition;
import com.sun.javadoc.Tag;
import com.sun.javadoc.Type;
import com.sun.tools.doclets.Taglet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * DesignPatternTaglet processes the {@literal @design.pattern} tag.
 * <p>
 * This tag can be used to document the design pattern of a type. The
 * description of the design pattern only needs to be written once for
 * the instantiating type of the design pattern, the description is copied to
 * all other participating types.
 * <p>
 * This tag can only be used in the Javadoc comment of a type declaration.
 * The tag consists of a header and an optional description. 
 * <p>
 * The header specifies the following properties:
 * <dl>
 * <dt>instantiatingType</dt><dd>The Java type which instantiates the
 * design pattern.</dd>
 * <dt>patternName</dt><dd>The name of the design pattern.</dd>
 * <dt>patternRole</dt><dd>The role of this type in the design pattern</dd>
 * </dl>
 * <p>
 * This taglet prints the name of a design pattern, the role of the type in
 * the design pattern, and the description. If no description is supplied,
 * then the description of the {@code instantiatingType} type is copied.
 * 
 * <p>
 * EBNF syntax of the tag:
 * <pre>
 * tag = "@design.pattern" , header , [ "." , description ] ;
 *
 * header = instantiatingType, " ", patternName , [ "," , patternRole ] ;
 *
 * instantiatingType = JavaTypeName ;
 * patternName = String ;
 * patternRole = String ;
 * description = (TextTag | SeeTag) , { TextTag | SeeTag };
 * </pre>
 *
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class DesignPatternTaglet implements Taglet {

    public static final String NAME = "design.pattern";
    private static final String HEADER = "Design Patterns:";

    private HashMap<String, ArrayList<Tag>> descriptions = new HashMap<>();

    /**
     * Return the name of this custom tag.
     */
    @Override
    public String getName() {
        return NAME;
    }

    /**
     * Will return false.
     *
     * @return false
     */
    @Override
    public boolean inField() {
        return false;
    }

    /**
     * Will return false.
     *
     * @return false
     */
    @Override
    public boolean inConstructor() {
        return false;
    }

    /**
     * Will return false.
     *
     * @return false
     */
    @Override
    public boolean inMethod() {
        return false;
    }

    /**
     * Will return false.
     *
     * @return false
     */
    @Override
    public boolean inOverview() {
        return false;
    }

    /**
     * Will return false.
     *
     * @return false
     */
    @Override
    public boolean inPackage() {
        return false;
    }

    /**
     * Will return true.
     *
     * @return true
     */
    @Override
    public boolean inType() {
        return true;
    }

    /**
     * Will return false.
     *
     * @return false
     */
    @Override
    public boolean isInlineTag() {
        return false;
    }

    /**
     * Register this Taglet.
     *
     * @param tagletMap the map to register this tag to.
     */
    public static void register(Map<String, Taglet> tagletMap) {
        DesignPatternTaglet tag = new DesignPatternTaglet();
        Taglet t = tagletMap.get(tag.getName());
        if (t != null) {
            tagletMap.remove(tag.getName());
        }
        tagletMap.put(tag.getName(), tag);
    }

    /**
     * Given the <code>Tag</code> representation of this custom tag, return its
     * string representation.
     *
     * @param tag the <code>Tag</code> representation of this custom tag.
     */
    @Override
    public String toString(Tag tag) {
        return toString(new Tag[]{tag});
    }

    /**
     * Given an array of <code>Tag</code>s representing this custom tag, return
     * its string representation.
     *
     * @param tags the array of <code>Tag</code>s representing of this custom
     * tag.
     */
    @Override
    public String toString(Tag[] tags) {
        if (tags.length == 0) {
            return "";
        }
        StringBuilder result = new StringBuilder();
        result.append("<hr>\n");
        result.append("<div class=\"block\">");
        for (int i = 0; i < tags.length; i++) {
            result.append("<p>");
            for (Tag parsedTag : lookupDescription(parseInlineTags(tags[i]))) {
                switch (parsedTag.kind()) {
                    case NAME:
                        DesignPatternHeaderTag dpt = (DesignPatternHeaderTag) parsedTag;
                        result.append("<b>Design Pattern:</b> ")//
                                // .append(dpt.instantiatingType).append(" ")//
                                .append(dpt.patternName);
                        if (dpt.patternRole.length() > 0) {
                            result.append(", <b>Role:</b> ").append(dpt.patternRole);
                        }
                        result.append('.');
                        result.append("<br>");
                        break;
                    case "@see":
                        if (parsedTag instanceof SeeTag) {
                            SeeTag see = (SeeTag) parsedTag;
                            String href = see.referencedClassName().replace('.', '/');
                            String label = see.label();
                            if (label.isEmpty()) {
                                label = see.referencedClassName();
                                int p = label.lastIndexOf('.');
                                label = label.substring(p + 1);
                            }

                            result.append("<a href=\"");
                            Doc holder = tags[0].holder();
                            if (holder instanceof Type) {
                                Type type = (Type) holder;
                                String qualifiedName = type.qualifiedTypeName();
                                for (int p = qualifiedName.indexOf('.'); p != -1; p = qualifiedName.indexOf('.', p + 1)) {
                                    result.append("../");
                                }
                            }
                            result.append(href)//
                                    .append(".html\">");
                            result.append(label)//
                                    .append("</a>");
                        } else {
                            result.append(parsedTag.text());
                        }
                        break;
                    default:
                        result.append(parsedTag.text());
                        break;
                }
            }
            /*
            String[] textParts = splitText(tags[i]);
            result.append("<b>Design Pattern:</b> ").append(textParts[0]);
            if (textParts[1].length() > 0) {
                result.append(", <b>Role:</b> ").append(textParts[1]);
            }
            result.append('.');

            String description;
            if (textParts[2].length() > 0) {
                description = textParts[2];
                descriptions.put(textParts[0], textParts[2]);
            } else {
                description = descriptions.get(textParts[0]);
                if (description == null) {
                    description = "";
                }
            }

            if (description.length() > 0) {

                Doc holder = tags[0].holder();
                if (holder instanceof Type) {
                    Type type = (Type) holder;
                    String qualifiedName = type.qualifiedTypeName();
                    String href = "";
                    for (int p = qualifiedName.indexOf('.'); p != -1; p = qualifiedName.indexOf('.', p + 1)) {
                        href = "../" + href;
                    }
                    description = description.replaceAll("href=\"", "href=\"" + href);
                }

                result.append("<br>").append(description);
            }*/

            result.append("</p>");
        }
        result.append("</div");
        result.append("<hr>\n");
        return result.toString();
    }

    public static class DesignPatternHeaderTag extends CompositeTag {
        /** The qualified name of the instantiating type. */
        private String instantiatingType;
        private String patternName;
        private String patternRole;

        public DesignPatternHeaderTag(Doc holder, String instantiatingType, String patternName, String patternRole, SourcePosition position) {
            super(holder, new Tag[0], position);
            this.instantiatingType = instantiatingType;
            this.patternName = patternName;
            this.patternRole = patternRole;
        }

        @Override
        public String kind() {
            return NAME;
        }
        
        public String unqualfiedInstantiatingType() {
            return toUnqualifiedName(instantiatingType);
        }

        public String getInstantiatingType() {
            return instantiatingType;
        }

        public String getPatternName() {
            return patternName;
        }

        public String getPatternRole() {
            return patternRole;
        }

    }

    /**
     * Tries to return a qualified name. Returns the unqualified name,
     * if it can not be looked up.
     * 
     * @param tag A tag which is used for lookup.
     * @param name A name which can be qualified or unqualified.
     * @return the qualified name if lookup was successful, otherwise returns
     * the unqualified nam.
     */
    public static String toQualifiedName(Tag tag, String name) {
        if (name.indexOf('.') != -1) {
            return name;
        }

        Doc doc = tag.holder();
        if (doc instanceof ClassDoc) {
            ClassDoc cd = (ClassDoc) doc;
            ClassDoc nameClass=cd.findClass(name);
            if (nameClass!=null) return nameClass.qualifiedName();
            
        }
        return name;
    }
   public static String toUnqualifiedName(String name) {
        return name.substring(name.lastIndexOf('.')+1);
                
    }


    private Tag[] lookupDescription(Tag[] tags) {
        if (tags.length > 0 && NAME.equals(tags[0].kind())) {
            DesignPatternHeaderTag dpt = (DesignPatternHeaderTag) tags[0];
            String key = dpt.instantiatingType + " " + dpt.patternName;
            if (tags.length == 1) {
                ArrayList<Tag> descr = descriptions.get(key);
                if (descr != null) {
                    Tag[] newTags = new Tag[descr.size() + 1];
                    newTags[0] = dpt;
                    for (int i = 1; i < newTags.length; i++) {
                        newTags[i] = descr.get(i - 1);
                    }
                    return newTags;
                } else {
                    Doc doc = dpt.holder();
                    if (doc instanceof ClassDoc) {
                        ClassDoc cd = (ClassDoc) doc;
                        ClassDoc maincd = cd.findClass(dpt.instantiatingType);
                        if (maincd != null) {
                            Tag[] lookupedTags = maincd.tags(NAME);
                            DesignPatternHeaderTag lookedUpDpt = null;
                            for (Tag lookup : lookupedTags) {
                                Tag[] inline = parseInlineTags(lookup);
                                if (inline.length > 0 && (inline[0] instanceof DesignPatternHeaderTag)) {
                                    lookedUpDpt = (DesignPatternHeaderTag) inline[0];
                                    if (lookedUpDpt.instantiatingType.equals(dpt.instantiatingType)
                                            && lookedUpDpt.patternName.equals(dpt.patternName)) {
                                        descr = new ArrayList<>();
                                        for (int i = 1; i < inline.length; i++) {
                                            descr.add(inline[i]);
                                        }
                                        break;
                                    }
                                }
                            }
                            if (descr != null) {
                                if (descr.isEmpty()) {
                                    System.err.println(lookedUpDpt.position() + ": warning: DesignPatternTaglet \"@" + NAME + " " + dpt.unqualfiedInstantiatingType() + " " + dpt.patternName + "\" must have a description.");
                                    descriptions.put(key, descr);
                                } else {
                                    descriptions.put(key, descr);
                                    ArrayList<Tag> result = new ArrayList<>();
                                    result.add(dpt);
                                    result.addAll(descr);
                                    return result.toArray(new Tag[result.size()]);
                                }
                            } else {
                                System.err.println(dpt.position() + ": warning: DesignPatternTaglet could not find a \"@" + NAME + " " + dpt.unqualfiedInstantiatingType() + " " + dpt.patternName + "\" tag in class " + dpt.instantiatingType + ".");
                            }
                        } else {
                            System.err.println(dpt.position() + ": warning: DesignPatternTaglet could not find class " + dpt.unqualfiedInstantiatingType() + ".");
                        }
                    }
                }

            } else {
                ArrayList<Tag> descr = new ArrayList<>();
                for (int i = 1; i < tags.length; i++) {
                    descr.add(tags[i]);
                }
                descriptions.put(key, descr);
            }
        }
        return tags;
    }

    /**
     * Parses the inline tags of a {@literal @design.pattern} Tag.
     * <p>
     * If parsing was successful, then the first tag in the returned array is a
     * DesignPatternHeaderTag. Subsequent contain the description of the
     * design pattern.
     * <p>
     * If parsing was unsuccessful, then the unparsed inline tags are returned.
     * 
     * @param tag A design pattern tag
     * @return The parsed inline tags
     */
    public static Tag[] parseInlineTags(Tag tag) {
        Tag[] inline = tag.inlineTags();
        if (inline.length == 0 || "@see".equals(inline[0].kind())) {
            return inline;
        }

        String text = inline[0].text();
        int p0 = text.indexOf(' ');
        int p1 = text.indexOf(',', p0 + 1);
        int p2 = text.indexOf('.', p1 + 1);

        String instantiatingType = "";
        String patternName = "";
        String patternRole = "";
        String description = "";

        if (p0 != -1 && p1 != -1 && p2 != -1) {
            instantiatingType = toQualifiedName(tag, text.substring(0, p0).trim());
            patternName = text.substring(p0 + 1, p1).trim();
            patternRole = text.substring(p1 + 1, p2).trim();
            description = text.substring(p2 + 1);
        } else {
            System.err.println(tag.position() + ": warning: DesignPatternTaglet illegal @" + NAME + " tag. Expected \"@" + NAME + " className patternName, roleName. description.\"");
            description = text;
        }

        ArrayList<Tag> parsed = new ArrayList<Tag>(inline.length + 1);
        DesignPatternHeaderTag dpt = new DesignPatternHeaderTag(inline[0].holder(), instantiatingType, patternName, patternRole, inline[0].position());
        parsed.add(dpt);
        if (!description.isEmpty()) {
            parsed.add(new TextTag(inline[0].holder(), description, inline[0].position()));
        }
        for (int i = 1; i < inline.length; i++) {
            parsed.add(inline[i]);
        }

        return parsed.toArray(new Tag[parsed.size()]);
    }
}
