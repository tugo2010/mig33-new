package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("mig33", appName)
  }

  @Test
  fun `verify mig_levels assets exist and decode`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    listOf(1, 2, 10, 11, 100, 101, 150).forEach { lvl ->
      context.assets.open("mig_levels/$lvl.png").use { stream ->
        val bmp = android.graphics.BitmapFactory.decodeStream(stream)
        org.junit.Assert.assertNotNull("Bitmap for level $lvl should not be null", bmp)
        org.junit.Assert.assertTrue(bmp.width > 0)
        org.junit.Assert.assertTrue(bmp.height > 0)
      }
    }
  }

  @Test
  fun `verify 2d avatar assets and config mappings exist`() {
    val defaultAvatar = com.example.model.AvatarConfig()
    assertEquals("bg_1", defaultAvatar.background)
    assertEquals("base_male_1", defaultAvatar.base)
    assertEquals("cloth_tshirt_white", defaultAvatar.clothes)
    assertEquals("hair_spiky_brown", defaultAvatar.hair)
    assertEquals("none", defaultAvatar.accessory)
    assertEquals("none", defaultAvatar.pet)

    org.junit.Assert.assertTrue(com.example.model.AvatarAssets.BACKGROUNDS.containsKey("bg_1"))
    org.junit.Assert.assertTrue(com.example.model.AvatarAssets.BASES.containsKey("base_male_1"))
    org.junit.Assert.assertTrue(com.example.model.AvatarAssets.CLOTHES.containsKey("cloth_tshirt_white"))
    org.junit.Assert.assertTrue(com.example.model.AvatarAssets.HAIRSTYLES.containsKey("hair_spiky_brown"))
    org.junit.Assert.assertTrue(com.example.model.AvatarAssets.ACCESSORIES.containsKey("none"))
    org.junit.Assert.assertTrue(com.example.model.AvatarAssets.PETS.containsKey("none"))
  }

  @Test
  fun `verify lucky wheel points and probabilities range from 10 to 50`() {
    val sectors = com.example.ui.components.LuckyWheelConfig.SECTORS
    org.junit.Assert.assertTrue("Should have sectors", sectors.isNotEmpty())
    sectors.forEach { sector ->
      org.junit.Assert.assertTrue("Points must be between 10 and 50", sector.points in 10..50)
      org.junit.Assert.assertTrue("Sector weight must be positive", sector.weight > 0)
    }

    // Check random generation stays within bounds
    repeat(50) {
      val index = com.example.ui.components.LuckyWheelConfig.getRandomSectorIndex()
      val selected = sectors[index]
      org.junit.Assert.assertTrue("Selected points must be between 10 and 50", selected.points in 10..50)
    }
  }
}
