package xyz.gofannon.mecha.impl;

import org.apache.commons.lang3.StringUtils;
import xyz.gofannon.mecha.api.Engine;
import xyz.gofannon.mecha.api.Platform;

public class PlatformImpl implements Platform {
    private Engine engine = new EngineImpl();

    @Override
    public String getPlatformType() {
        return StringUtils.deleteWhitespace("Dark           Cyborg");
    }

    @Override
    public Engine getEngine() {
        return engine;
    }

}
