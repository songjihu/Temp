package com.sjh.word.data.model;

/**
 * 单条word格式
 */
public class WordModel {

    private String wordId;//单词id
    private String wordUnit;//单词所属单元
    private String wordSelf;//单词本身
    private String wordMeaning;//单词意思

    public String getWordId() {
        return wordId;
    }

    public void setWordId(String wordId) {
        this.wordId = wordId;
    }

    public String getWordUnit() {
        return wordUnit;
    }

    public void setWordUnit(String wordUnit) {
        this.wordUnit = wordUnit;
    }

    public String getWordSelf() {
        return wordSelf;
    }

    public void setWordSelf(String wordSelf) {
        this.wordSelf = wordSelf;
    }

    public String getWordMeaning() {
        return wordMeaning;
    }

    public void setWordMeaning(String wordMeaning) {
        this.wordMeaning = wordMeaning;
    }

    public WordModel(String wordId,String wordUnit,String wordSelf,String wordMeaning) {
        this.wordId=wordId;
        this.wordUnit=wordUnit;
        this.wordSelf=wordSelf;
        this.wordMeaning=wordMeaning;
    }


}
