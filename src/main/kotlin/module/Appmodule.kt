package org.delcom.module

import org.delcom.repositories.IPlantRepository
import org.delcom.repositories.IXiaomiProductRepository
import org.delcom.repositories.PlantRepository
import org.delcom.repositories.XiaomiProductRepository
import org.delcom.services.PlantService
import org.delcom.services.ProfileService
import org.delcom.services.XiaomiProductService
import org.koin.dsl.module

val appModule = module {
    single<IPlantRepository> { PlantRepository() }
    single { PlantService(get()) }
    single { ProfileService() }
    single<IXiaomiProductRepository> { XiaomiProductRepository() }
    single { XiaomiProductService(get()) }
}