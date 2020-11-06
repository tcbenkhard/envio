package nl.benkhard.envio.util;

import com.google.common.base.CaseFormat;
import nl.benkhard.envio.model.DeclaredVariable;

public class NameUtils {

    public static String getMethodName(DeclaredVariable declaredVariable) {
        String methodName = CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL, declaredVariable.getName());

        return String.format("get%s", methodName);
    }
}
