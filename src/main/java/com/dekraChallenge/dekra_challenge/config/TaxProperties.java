package com.dekraChallenge.dekra_challenge.config;

import com.dekraChallenge.dekra_challenge.domain.tax.TaxType;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.tax")
public class TaxProperties {

    private TaxType type = TaxType.IVA;

    public TaxType getType() {
        return type;
    }

    public void setType(TaxType type) {
        this.type = type;
    }
}
