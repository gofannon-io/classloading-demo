package xyz.gofannon.mecha.application;

class LocalResource {
     String id;

    volatile Class<?> clazz = null;

    volatile byte[] content=null;

    LocalResource(String id, Class<?> clazz) {
        this.id = id;
        this.clazz = clazz;
    }
    LocalResource(String id,  byte[] content) {
        this.id = id;
        this.content = content;
    }
}
