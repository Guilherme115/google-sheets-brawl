package com.guilherme.ml.model

import jakarta.persistence.Id
import org.jetbrains.annotations.NotNull
import org.springframework.data.mongodb.core.mapping.Document


@Document(collection = "analysis")
data class BrawlerUsage(

    @NotNull
    val brawlerName : String,
    @NotNull
    val usageCount : Int,
)
