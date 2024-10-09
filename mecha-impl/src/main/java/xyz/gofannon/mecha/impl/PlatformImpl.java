package xyz.gofannon.mecha.impl;

import xyz.gofannon.mecha.api.Engine;
import xyz.gofannon.mecha.api.Platform;

public class PlatformImpl implements Platform {
    private Engine engine = new EngineImpl();

    @Override
    public String getPlatformType() {
        return "BigDroid";
    }

    @Override
    public Engine getEngine() {
        return engine;
    }

}
