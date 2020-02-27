package jackson;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.google.common.collect.ImmutableMap;
import reactor.core.publisher.Flux;

import java.io.IOException;
import java.util.Map;

/**
 * @author guoda
 * @date 2019-09-16
 * <p>
 * JAVA Json流式解析
 */
public class Stream {
    private static final Map<Integer, String> MAP = ImmutableMap.<Integer, String>builder()
                .put(1, "START_OBJECT")
                .put(2, "END_OBJECT")
                .put(3, "START_ARRAY")
                .put(4, "END_ARRAY")
                .put(5, "FIELD_NAME")
                .put(6, "STRING")
                .put(7, "NUMBER_INT")
                .build();
    /**
     * 测试用例
     *
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) {
        System.out.println("jsonParser.getCurrentName()      jsonParser.getText()      jsonParser.getValueAsString()");
        Flux.just("json.json")
                .map(Thread.currentThread().getContextClassLoader()::getResourceAsStream)
                .map(inputStream -> {
                    JsonFactory factory = new JsonFactory();
                    try {
                        return factory.createParser(inputStream);
                    } catch (Exception e) {
                        e.printStackTrace();
                        return null;
                    }
                })
                .flatMap(jsonParser -> Flux.create(fluxSink -> {
                    try {
                        while (jsonParser.nextToken() != null) {
                            fluxSink.next(jsonParser);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }))
                .filter(JsonParser.class::isInstance)
                .map(JsonParser.class::cast)
                .subscribe(jsonParser -> {
                    try {
                        System.out.println(MAP.get(jsonParser.currentTokenId()));
                        System.out.println(jsonParser.getCurrentName() + "  " + jsonParser.getText() + "  " + jsonParser.getValueAsString());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
    }
}
