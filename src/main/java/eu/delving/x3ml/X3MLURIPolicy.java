package eu.delving.x3ml;

import com.damnhandy.uri.template.MalformedUriTemplateException;
import com.damnhandy.uri.template.UriTemplate;
import com.damnhandy.uri.template.VariableExpansionException;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamImplicit;
import com.thoughtworks.xstream.converters.reflection.PureJavaReflectionProvider;
import com.thoughtworks.xstream.io.naming.NoNameCoder;
import com.thoughtworks.xstream.io.xml.XppDriver;

import java.io.InputStream;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Gerald de Jong <gerald@delving.eu>
 */

public class X3MLURIPolicy implements X3ML.URIPolicy {
    private static final Pattern BRACES = Pattern.compile("\\{[?;+#]?([^}]+)\\}");
    private Map<String, Template> templateMap = new TreeMap<String, Template>();

    public X3MLURIPolicy(InputStream inputStream) {
        Policy policy = (Policy) stream().fromXML(inputStream);
        for (Template template: policy.templates) {
            templateMap.put(template.name, template);
        }
    }

    @Override
    public String generateUri(String name, X3ML.URIArguments arguments) {
        if (name == null) name = arguments.getArgument(X3ML.CLASS_NAME);
        Template template = templateMap.get(name);
        if (template == null) throw new X3MLException("No template for "+name);
        try {
            UriTemplate uriTemplate = UriTemplate.fromTemplate(template.pattern);
            for (String variable : variablesFromPattern(template.pattern)) {
                uriTemplate.set(variable, arguments.getArgument(variable));
            }
            return uriTemplate.expand();
        }
        catch (MalformedUriTemplateException e) {
            throw new X3MLException("Malformed", e);
        }
        catch (VariableExpansionException e) {
            throw new X3MLException("Variable", e);
        }
    }

    // == the rest is for the XML form

    private static List<String> variablesFromPattern(String pattern) {
        Matcher braces = BRACES.matcher(pattern);
        List<String> variables = new ArrayList<String>();
        while (braces.find()) {
            Collections.addAll(variables, braces.group(1).split(","));
        }
        return variables;
    }

    private static XStream stream() {
        XStream xstream = new XStream(new PureJavaReflectionProvider(), new XppDriver(new NoNameCoder()));
        xstream.setMode(XStream.NO_REFERENCES);
        xstream.processAnnotations(Policy.class);
        return xstream;
    }

    @XStreamAlias("uri-policy")
    public static class Policy {
        @XStreamImplicit
        List<Template> templates;
    }

    @XStreamAlias("template")
    public static class Template {
        @XStreamAsAttribute
        public String name;

        public String pattern;

        public String variables;

        public String toString() {
            return pattern;
        }
    }


}
