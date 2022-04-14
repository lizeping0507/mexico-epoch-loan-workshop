package com.epoch.loan.workshop.common.util;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.List;
import java.util.Map;


/**
 * 简单json工具类,使用之前必须先
 * 使用setObjectMapper()方法注入ObjectMapper对象实例
 */
public class JsonUtils {

    /**
     * 默认日期时间格式
     */
    public static final String DEFAULT_DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    /**
     * 默认日期格式
     */
    public static final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd";
    /**
     * 默认时间格式
     */
    public static final String DEFAULT_TIME_FORMAT = "HH:mm:ss";

    private static ObjectMapper objectMapper;

    public static void setObjectMapper(final ObjectMapper mapper) {
        JsonUtils.objectMapper = mapper;
    }

    public static ObjectMapper jacksonObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setSerializationInclusion(Include.NON_DEFAULT);
        objectMapper.setSerializationInclusion(Include.NON_NULL);
        objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        //objectMapper.setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);

        // SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.sss'Z'");
        // df.setTimeZone(TimeZone.getTimeZone("UTC"));
        // objectMapper.setDateFormat(df);
        //DateFormat dateFormat = objectMapper.getDateFormat();
        //objectMapper.setDateFormat(new com.yinda.platform.util.spring.CustomDateFormat(dateFormat));


//        SimpleModule module = new SimpleModule();
//        module.addDeserializer(Date.class, new JsonDateDeserialize());
//        objectMapper.registerModule(module);

        JavaTimeModule javaTimeModule = new JavaTimeModule();
        javaTimeModule.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(DateTimeFormatter.ofPattern(DEFAULT_DATE_TIME_FORMAT)));
        javaTimeModule.addSerializer(LocalDate.class, new LocalDateSerializer(DateTimeFormatter.ofPattern(DEFAULT_DATE_FORMAT)));
        javaTimeModule.addSerializer(LocalTime.class, new LocalTimeSerializer(DateTimeFormatter.ofPattern(DEFAULT_TIME_FORMAT)));
        javaTimeModule.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(DateTimeFormatter.ofPattern(DEFAULT_DATE_TIME_FORMAT)));
        javaTimeModule.addDeserializer(LocalDate.class, new LocalDateDeserializer(DateTimeFormatter.ofPattern(DEFAULT_DATE_FORMAT)));
        javaTimeModule.addDeserializer(LocalTime.class, new LocalTimeDeserializer(DateTimeFormatter.ofPattern(DEFAULT_TIME_FORMAT)));

//        //Date序列化和反序列化
//        javaTimeModule.addSerializer(Date.class, new JsonSerializer<Date>() {
//            @Override
//            public void serialize(Date date, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
//                SimpleDateFormat formatter = new SimpleDateFormat(DEFAULT_DATE_TIME_FORMAT);
//                String formattedDate = formatter.format(date);
//                jsonGenerator.writeString(formattedDate);
//            }
//        });
//        javaTimeModule.addDeserializer(Date.class, new JsonDeserializer<Date>() {
//            @Override
//            public Date deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
//                SimpleDateFormat format = new SimpleDateFormat(DEFAULT_DATE_TIME_FORMAT);
//                String date = jsonParser.getText();
//                try {
//                    return format.parse(date);
//                } catch (ParseException e) {
//                    throw new RuntimeException(e);
//                }
//            }
//        });
//        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        objectMapper.registerModule(javaTimeModule);

        return objectMapper;
    }

//	static class JsonDateDeserialize extends JsonDeserializer {
//	    // 按照此优先级顺序尝试转换日期格式，建议从配置文件加载
//	    private static final String[] patterns = {"yyyy-MM-dd HH:mm:ss","yyyy-MM-dd'T'HH:mm:ssZ", "yyyy-MM-dd'T'HH:mm:ss'Z'", "yyyy-MM-dd", "yyyy-MM-dd'T'HH:mm:ss"};
//
//	    @Override
//		public Date deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
//	        String dateAsString = jp.getText();
//	        Date parseDate = null;
//	        if (dateAsString.contains("-")) {
//	            // 日期类型
//	            try {
//	                parseDate = DateUtils.parseDate(dateAsString, patterns);
//	            } catch (ParseException e) {
//	                //log.error(String.format("不支持的日期格式: %s, error=%s", dateAsString, e.getMessage()), e);
//	            	throw new RuntimeException(String.format("不支持的日期格式: %s, error=%s", dateAsString, e.getMessage()),e);
//	            }
//	        } else {
//	            // long毫秒
//	            try {
//	                long time = Long.valueOf(dateAsString);
//	                parseDate = new Date(time);
//	            } catch (NumberFormatException e) {
//	                //log.error(String.format("日期格式非数字: %s, error=%s", dateAsString, e.getMessage()), e);
//	            	throw new RuntimeException(String.format("不支持的日期格式: %s, error=%s", dateAsString, e.getMessage()),e);
//	            }
//	        }
//	        return parseDate;
//	    }
//	}	

    public static String toJson(Object bean) {
        try {
            return objectMapper.writeValueAsString(bean);
        } catch (Exception e) {
            //e.printStackTrace();
            throw new RuntimeException(e);
        }

    }

    public static <T> T fromJson(String json, Class<T> clazz) {
        try {
            return objectMapper.readValue(json, clazz);
        } catch (Exception e) {
            //e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public static <T> T fromJson(String json, TypeReference<T> javaType) {
        try {
            return objectMapper.readValue(json, javaType);
        } catch (Exception e) {
            //e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public static <T> T fromJson(String json, JavaType javaType) {
        try {
            return objectMapper.readValue(json, javaType);
        } catch (Exception e) {
            //e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> List<T> fromListJson(String json, Class<?> collectionClass, Class<?>... elementClasses) {
        JavaType javaType = objectMapper.getTypeFactory().constructParametricType(collectionClass, elementClasses);
        try {
            return (List<T>) objectMapper.readValue(json, javaType);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public static JavaType buildMapType(Class<? extends Map> mapClass, Class<?> keyClass, Class<?> valueClass) {
        return objectMapper.getTypeFactory().constructMapType(mapClass, keyClass, valueClass);
    }

    public static JavaType buildCollectionType(Class<? extends Collection> collectionClass, Class<?> elementClass) {
        return objectMapper.getTypeFactory().constructCollectionType(collectionClass, elementClass);
    }
}