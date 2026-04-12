package com.wechantloup.spritehandler.spriteCreation

enum class GenerationState {
    IDLE,
    CHECKING_PALETTE,
    GENERATING_IMAGES,
    ENCODING,
    DONE,
    ERROR
}
