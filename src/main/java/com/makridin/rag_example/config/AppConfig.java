package com.makridin.rag_example.config;

import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.reader.TextReader;
import org.springframework.ai.transformer.splitter.TextSplitter;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

import java.io.File;
import java.util.List;

@Configuration
public class AppConfig {
    @Value("classpath:newproducts.txt")
    private Resource newProductResource;

    @Bean
    public SimpleVectorStore simpleVectorStore(EmbeddingModel embeddingModel) {
        SimpleVectorStore store = SimpleVectorStore.builder(embeddingModel).build();
        File file = new File("src/main/resources/newproducts.json");
        if (file.exists()) {
            store.load(file);
        } else {
            TextReader reader = new TextReader(newProductResource);
            reader.getCustomMetadata().put("filename", "newproducts.txt");
            List<Document> documents = reader.get();
            TextSplitter splitter = new TokenTextSplitter();
            List<Document> splittedDocuments = splitter.apply(documents);
            store.add(splittedDocuments);
            store.save(file);
        }
        return store;
    }
}
