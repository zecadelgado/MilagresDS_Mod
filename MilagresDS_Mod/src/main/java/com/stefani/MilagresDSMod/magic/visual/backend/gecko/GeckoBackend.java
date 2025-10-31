package com.stefani.MilagresDSMod.magic.visual.backend.gecko;

/*
 * Placeholder backend for GeckoLib integration.
 *
 * Quando GeckoLib estiver disponível, converta este stub em implementação real:
 * - Criar renderers que estendam GeoEntityRenderer para LightningSpearEntity, FlameSlingEntity e HealAreaEntity.
 * - Carregar os modelos JSON em assets/milagresdsmod/geo/*.geo.json e animações em assets/milagresdsmod/animations/*.anim.json.
 * - Registrar as animações idle correspondentes (Lightning → animation.lightning_spear.idle, Flame → animation.flame_sling.idle,
 *   Heal → animation.heal_ring.breathe) usando AnimationController.
 * - Detectar a presença de GeckoLib via ModList.get().isLoaded("geckolib") antes de registrar.
 *
 * A assinatura esperada pelo RendererRegistry é um método estático:
 *
 *     public static boolean registerRenderers(EntityRenderersEvent.RegisterRenderers event)
 *
 * Retorne true caso os renderers Gecko tenham sido registrados com sucesso, permitindo que o fallback vanilla seja pulado.
 */
