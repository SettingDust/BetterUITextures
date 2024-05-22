## Feature

Using reusable textures for mods instead of hardcoded ones.  
It's data-driven.  
So, you can modify the textures generated with the resource pack.  
Also, add compatibility to any mod with any resource packs.  
For JSON
example: https://github.com/SettingDust/DynamicTextures/tree/main/mod/src/main/generated/assets/dynamic-textures/dynamic_texture/modifier

## Supported

<details>
<summary>Fabric Waystones</summary>


![fwaystones_0](https://raw.githubusercontent.com/SettingDust/DynamicTextures/main/docs/fwaystones_0.png) ![fwaystones_1](https://raw.githubusercontent.com/SettingDust/DynamicTextures/main/docs/fwaystones_1.png)


</details>

<details>
<summary>Spell Engine</summary>

![spell_engine_0](https://raw.githubusercontent.com/SettingDust/DynamicTextures/main/docs/spell_engine_0.png) ![spell_engine_1](https://raw.githubusercontent.com/SettingDust/DynamicTextures/main/docs/spell_engine_1.png)


</details>

<details>
<summary>Runes</summary>

Lazy

</details>

<details>
<summary>Farmer's Delight</summary>

Lazy

</details>

<details>
<summary>Pack It Up</summary>

Lazy

</details>

<details>
<summary>Illager Invasion</summary>

Lazy

</details>

<details>
<summary>Amethyst Imbuement</summary>

![amethyst_imbuement_0](https://raw.githubusercontent.com/SettingDust/DynamicTextures/main/docs/amethyst_imbuement_0.png) ![amethyst_imbuement_1](https://raw.githubusercontent.com/SettingDust/DynamicTextures/main/docs/amethyst_imbuement_1.png)


</details>

## How to support a mod by yourself?

- You need a tool that can show you the sizes of elements you need on the image.
- You can [reload the resource](https://minecraft.wiki/w/Debug_screen#More_debug_keys) to see the changes. 
- I will explain the built-in jsons here.
- ### https://github.com/SettingDust/DynamicTextures/blob/main/mod/src/main/generated/assets/dynamic-textures/dynamic_texture/modifier/spell_engine/spell_binding.json
    - #### `modId`
        - Loading this texture modifier when the mod is loaded.
        - Texture modifier is for modify the target texture for generating the texture dynamically. It's the main part
          of this mod.
    - #### `size`
        - Define the size of the texture. Isn't required when specified `targetTexture`.
        - When there isn't `targetTexture`. It will generate a texture with only transparent pixels.
    - #### `targetTexture`
        - The path for generated texture. Isn't required when specified `size`.
        - We need the original texture to find out what modifiers we need.
        - Open the jar of mod and find out the png file path
          is `spell_engine-0.12.4_1.20.1.jar!\assets\spell_engine\textures\gui\spell_binding.png`
        - ![raw_spell_binding](https://raw.githubusercontent.com/SettingDust/DynamicTextures/main/docs/raw_spell_binding.png)
        - So we know the texture's [resource location](https://minecraft.wiki/w/Resource_location)
          is `spell_engine:gui/spell_binding`. It's the location of the `targetTexture` we need.
    - #### `modifiers`
        - An array of modifiers that will apply to the raw texture.
        - ##### 1. `dynamic-textures:remove_border`
            - Remove border from the texture
            - The values here removed the border of the gui background.
            - `rect`
                - The target rect you need at the texture.
                - The value here means we selected the gui background.
            - `border`
                - An object that holds two sizes that confirms the size of the borders.
                - `first`
                    - The size of the top and left border. That is, the distance between the inner edge of the frame and
                      the left and top edges.
                - `second`
                    - The size of the bottom and right border. That is, the distance between the inner edge of the frame
                      and the right and bottom edges.
        - ##### 2. `dynamic-textures:remove_color`
            - Remove specific color from specific rect.
            - The values here removed background color from the gui.
            - `color`
                - A ARGB color.
                - `ffc6c6c6` is the background color of gui
        - ##### 3. `dynamic-textures:copy_rect`
            - Copy a rect on source texture onto specific rect.
            - `sourceTexture`
                - A resource location of texture
                - `dynamic-textures:dynamic_texture/defined/inventory/bottom` is
                  an [defined](https://github.com/SettingDust/DynamicTextures/tree/main/mod/src/main/generated/assets/dynamic-textures/dynamic_texture/defined)
                  texture that will generate runtime for matching the resource pack. You can add your own defined
                  texture.
            - `fromRect` It's all content from source texture by default. So, emit here.
            - `targetRect`
                - The copy target
                - The values here are targeting the bottom part of the gui that contains the player inventory.
        - ##### 4. `dynamic-textures:remove_rect`
            - Remove target rect from current texture
            - `rect` The values here removed the left slot on the top part of the gui
        - ##### 5. `dynamic-textures:remove_border`
            - The values here removed the border of the right slot on the gui
        - ##### 6. `dynamic-textures:remove_color`
            - The values here removed the background of the right slot.
        - ##### 7. `dynamic-textures:overlay`
            - A bit complex. It's blending the result of sourceTextures with current texture
            - The values here make the current texture blend with the defined abreast slots
            - `onExisting` Only blend the pixel alpha not 0 when true. Default is false
            - `invert`
                - If false, source texture on current texture. If true, current texture on source texture. Default is
                  false.
                - For display the icon in the slot. It's true here.
            - `sourceTextures` An array of textures to blend. Applying by order. The properties are as same as the root
              object.
        - ##### 8. `dynamic-textures:copy_rect`
            - Replace the enchanting elements background of the gui on the texture
        - ##### 9. `dynamic-textures:overlay`
            - The values applied the default background of the enchanting elements on the right of the gui in the
              texture
        - ##### 10. `dynamic-textures:copy_rect`
            - Replace the statuses on the texture
        - ##### 11. `dynamic-textures:overlay`
            - Blend the inventory top background with current texture
            - `sourceTextures.modifiers`
                - `dynamic-textures:copy_nine_patch`
                    - Copy the source texture with [nine patch](https://en.wikipedia.org/wiki/9-slice_scaling) to avoid
                      stretch.
                    - `sourceRect` default is the whole texture.
                    - `repeat` default is false.
