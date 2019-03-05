package com.pulan.dialogserver.entity.tulin;

public class Perception {
    private InputText inputText;
    private InputImage inputImage;
    private SelfInfo selfInfo;


    public Perception() {
    }

    public Perception(InputText inputText, InputImage inputImage, SelfInfo selfInfo) {
        this.inputText = inputText;
        this.inputImage = inputImage;
        this.selfInfo = selfInfo;
    }

    public InputImage getInputImage() {
        return inputImage;
    }

    public void setInputImage(InputImage inputImage) {
        this.inputImage = inputImage;
    }

    public SelfInfo getSelfInfo() {
        return selfInfo;
    }

    public void setSelfInfo(SelfInfo selfInfo) {
        this.selfInfo = selfInfo;
    }

    public InputText getInputText() {
        return inputText;
    }

    public void setInputText(InputText inputText) {
        this.inputText = inputText;
    }
}
