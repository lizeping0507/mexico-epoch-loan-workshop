package com.epoch.loan.workshop.common.constant;

/**
 * @author : Shangkunfeng
 * @packageName : com.epoch.loan.workshop.common.constant;
 * @className : ResultEnum
 * @createTime : 2022/3/22 14:28
 * @description : 响应信息枚举
 */
public enum ResultEnum {
    /**
     * 请求成功
     */
    SUCCESS(2000, "Éxito "),

    // ======= 重定向 需要进一步操作完成请求 ========
    /**
     * 默认重定向
     */
    REDIRECT(3002, " Solicitar redirección "),

    // ======= 客户端错误 ========
    /**
     * 客户端错误
     */
    CLIENT_ERROR(4000, " Error del cliente "),
    /**
     * 请求方式错误
     */
    METHOD_ERROR(4001, " Error de método "),
    /**
     * 请求参数错误
     */
    PARAM_ERROR(4002, " Error en los parámetros "),
    /**
     * 缺少必要参数
     */
    MISSING_REQUIRED_PARAMS(4003, " Falta de parámetros requeridos "),
    /**
     * 数据不存在
     */
    NO_EXITS(4004, " Los datos no existen "),
    /**
     * 需要登录
     */
    NO_LOGIN(4005, "Por favor, inicie sesión de nuevo"),
    /**
     * 版本异常 需要升级
     */
    VERSION_ERROR(4006, " Estimados clientes, hemos añadido algunas funciones en la última versión. Lamentablemente, esta versión actual no es compatible. Es necesario actualizar la aplicación antes de proceder."),
    /**
     * url 未映射
     */
    URL_NOT_MAPPING(4007, " URL no está mapeada "),
    /**
     * 手机号不存在
     */
    PHONE_NO_EXIT(4008, " Número de teléfono no registrado, por favor introduzca el correcto "),
    /**
     * 密码不正确
     */
    PASSWORD_INVALID(4009, "La contraseña es incorrecta, por favor, inténtelo de nuevo"),
    /**
     * 手机号已存在
     */
    PHONE_EXIT(4010, " Este número de móvil ya existe"),
    /**
     * 验证码错误
     */
    SMS_CODE_ERROR(4011, " Error de OTP, por favor, inténtelo de nuevo"),

    /**
     * 还款账户异常
     */
    REMITTANCE_ACCOUNT_ERROR(4012, " Error en la cuenta "),

    /**
     * 订单异常
     */
    ORDER_ERROR(4013, " Error de pedido "),

    /**
     * 验证码发送错误
     */
    SMS_CODE_SEND_FAILED(4011, " SMS Code envío fallido "),

    /**
     * 设备已注册过其他账户
     */
    DEVICE_REGISTERED(4014, " El dispositivo que utiliza ha registrado otros números de teléfono y no se puede registrar."),

    /**
     * 银行卡数字应为16位，clabe为18位， 位数不对
     */
    BANK_CART_NUMBER_ERROR(4012, " La tarjeta bancaria debe tener 16 dígitos,La de CLABE debe tener 18 dígitos"),

    // ======= 服务端错误 ========
    /**
     * 服务端错误
     */
    SERVER_ERROR(5000, " Error del servidor "),

    /**
     * 服务端错误
     */
    SERVICE_ERROR(5001, " Errores del lado del servidor "),

    /**
     * 系统错误
     */
    SYSTEM_ERROR(5002, " Excepción del sistema, por favor, inténtelo más tarde "),

    /**
     * 超时
     */
    TIMEOUT_ERROR(5003, " Tiempo de espera de la solicitud "),

    /**
     * 同步错误
     */
    SYNCHRONIZATION_ERROR(5004, " Error de sincronización "),

    // ======= KYC错误========

    /**
     * 证件扫描失败，请重新扫描证件
     */
    KYC_SCAN_CARD_ERROR(6000, " El escaneo de la tarjeta de identificación falló, por favor escanee su tarjeta de identificación de nuevo."),

    /**
     * 活体检测失败，请重新上传
     */
    KYC_Liveness_ERROR(6001, " La Detección de vida falló, Vuelve a intentarlo.."),

    /**
     * 人脸匹配失败，请重新上传图片
     */
    KYC_FACE_COMPARISON_ERROR(6002, " La coincidencia de caras ha fallado, Suba la imagen de nuevo, por favor."),

    /**
     * 上传文件失败
     */
    KYC_UPLOAD_FILE_ERROR(6003, " No se ha podido subir el archivo, por favor, vuelva a subir la imagen."),

    /**
     * Rfc或者INE/IFE格式错误
     */
    RFC_INF_ERROR(6010, " Error de formato RFC o INE/IFE."),

    /**
     * INE/IFE证件id 已经被认证了
     */
    INF_CERTIFIED_ERROR(6011, "INE/IFE ha sido certificado."),

    /**
     * INE/IFE证件id 已经被其他用户认证了
     */
    INF_CERTIFIED_OTHER_ERROR(6012, " El INE/IFE ha sido autentificado por otros usuarios "),

    /**
     * RFC 已经被认证了
     */
    RFC_CERTIFIED_ERROR(6013, "RFC ha sido certificado."),

    /**
     * RFC 已经被其他用户认证了
     */
    RFC_CERTIFIED_OTHER_ERROR(6014, " El RFC ha sido autentificado por otros usuarios "),

    /**
     * 根据手机号查询 当前账号认证信息与主账户不匹配
     */
    RFC_INF_CERTIFIED_ERROR(6015, " La información de autentificación de la cuenta actual no coincide con la cuenta principal meidante la Búsqueda por número de teléfono móvil "),

    /**
     * 根据INE/IFE证件id查询 当前账号认证信息与主账户不匹配
     */
    RFC_MOBILE_CERTIFIED_ERROR(6016, " La información de autentificación de la cuenta actual no coincide con la cuenta principal meidante la Búsqueda por INE/IFE ID "),

    /**
     * 根据RFC查询 当前账号认证信息与主账户不匹配
     */
    INF_MOBILE_CERTIFIED_OTHER_ERROR(6017, " La información de autentificación de la cuenta actual no coincide con la cuenta principal meidante la Búsqueda por RFC "),

    // 业务错误
    // 业务错误
    /**
     * 被拒
     */
    REJECTED_ERROR(7000, " Rechazado "),

    /**
     * 多投被拒
     */
    DELIVERY_REJECTED_ERROR(7001, "Hay varios préstamos en curso y esta solicitud fue rechazada"),

    /**
     * 冷却期
     */
    COOLING_PERIOD(7002, " Periodo de enfriamiento "),

    /**
     * 与风控交互出错
     */
    LOAN_RISK_EACH_ERROR(9001, "Error al interactuar con el servicio de control de riesgos de terceros"),

    /**
     * CURP校验失败
     */
    RFC_CHECK_CURP_ERROR(9002, " La verificación de la CURP ha fallado "),

    /**
     * 无可用聚道
     */
    CHANNEL_UN_DO_ERROR(10000, "No hay proveedores de servicios OCR disponibles para este canal "),

    /**
     * 设备已注册过其他账户
     */
    ACCOUNT_BEEN_USED_ERRO(4014, "The bank card has been used by someone else."),
    ;




    private Integer code;
    private String message;

    ResultEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public Integer code() {
        return code;
    }

    public String message() {
        return message;
    }
}
