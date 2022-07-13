package apache;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.text.StringSubstitutor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;

import java.util.HashMap;
import java.util.Map;

@Slf4j
public class TextDemo {
    @Test
    @DisplayName("Replace")
    public void replace() {
        // Build map
        Map<String, String> valuesMap = new HashMap<>();
        valuesMap.put("animal", "quick brown fox");
        valuesMap.put("target", "lazy dog");
        String templateString = "The ${animal} jumped over the ${target} ${undefined.number:-1234567890} times.";

        // Build StringSubstitutor
        StringSubstitutor sub = new StringSubstitutor(valuesMap);

        // Replace
        String resolvedString = sub.replace(templateString);

        // print & assert
        log.info(resolvedString);
        Assertions.assertEquals(resolvedString, "The quick brown fox jumped over the lazy dog 1234567890 times.");
    }

    @Test
    @DisplayName("Using Interpolation")
    public void interpolator() {
        final StringSubstitutor interpolator = StringSubstitutor.createInterpolator();
        interpolator.setEnableSubstitutionInVariables(true); // Allows for nested $'s.
        final String text = interpolator.replace("Base64 Decoder:        ${base64Decoder:SGVsbG9Xb3JsZCE=}\n"
                + "Base64 Encoder:        ${base64Encoder:HelloWorld!}\n"
                + "Java Constant:         ${const:java.awt.event.KeyEvent.VK_ESCAPE}\n"
                + "Date:                  ${date:yyyy-MM-dd}\n" + "DNS:                   ${dns:address|apache.org}\n"
                + "Environment Variable:  ${env:USERNAME}\n"
                + "File Content:          ${file:UTF-8:src/test/resources/document.properties}\n"
                + "Java:                  ${java:version}\n" + "Localhost:             ${localhost:canonical-name}\n"
                + "Properties File:       ${properties:src/test/resources/document.properties::mykey}\n"
                + "Resource Bundle:       ${resourceBundle:org.example.testResourceBundleLookup:mykey}\n"
                + "Script:                ${script:javascript:3 + 4}\n" + "System Property:       ${sys:user.dir}\n"
                + "URL Decoder:           ${urlDecoder:Hello%20World%21}\n"
                + "URL Encoder:           ${urlEncoder:Hello World!}\n"
                + "URL Content (HTTP):    ${url:UTF-8:http://www.baidu.com}\n"
                + "URL Content (HTTPS):   ${url:UTF-8:https://www.baidu.com}\n"
                + "URL Content (File):    ${url:UTF-8:file:///${sys:user.dir}/src/test/resources/document.properties}\n"
                + "XML XPath:             ${xml:src/test/resources/document.xml:/root/path/to/node}\n");
        log.info(text);
        Assertions.assertTrue(true);
    }

    @Test
    @DisplayName("Using Recursive Variable Replacement")
    public void variable() {
        Map<String, String> valuesMap = new HashMap<>();
        valuesMap.put("name", "key");
        valuesMap.put("key", "value");

        // Build StringSubstitutor
        StringSubstitutor sub = new StringSubstitutor(valuesMap);
        sub.setEnableSubstitutionInVariables(true); // Allows for nested $'s.
        String templateString = "The Value is : ${${name}}.";

        String replace = sub.replace(templateString);

        log.info(replace);
        Assertions.assertEquals(replace, "The Value is : value.");
    }
}
