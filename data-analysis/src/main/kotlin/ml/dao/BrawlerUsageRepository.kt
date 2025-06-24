package com.guilherme.ml.dao

import com.guilherme.ml.model.BrawlerUsage
import org.springframework.data.mongodb.repository.MongoRepository

interface BrawlerUsageRepository : MongoRepository<BrawlerUsage, Long> {
}