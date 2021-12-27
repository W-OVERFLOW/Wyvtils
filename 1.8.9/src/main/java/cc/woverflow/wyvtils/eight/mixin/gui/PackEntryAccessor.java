/*
 * Wyvtils, a utility mod for 1.8.9.
 * Copyright (C) 2021 Rysm
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

/*
 * Rysm, a utility mod for 1.8.9.
 * Copyright (C) 2021 Rysm
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package cc.woverflow.wyvtils.eight.mixin.gui;

import net.minecraft.client.resources.ResourcePackRepository;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ResourcePackRepository.Entry.class)
public class PackEntryAccessor {
    /*/

    @Shadow @Final private File resourcePackFile;


    @Shadow private IResourcePack reResourcePack;

    @Redirect(method = "updateResourcePack", at = @At(value = "FIELD", target = "Lnet/minecraft/client/resources/ResourcePackRepository$Entry;reResourcePack:Lnet/minecraft/client/resources/IResourcePack;", opcode = Opcodes.PUTFIELD))
    private void redirect(ResourcePackRepository.Entry instance, IResourcePack value) {
        if (isRysmFolder()) {
            this.reResourcePack = new RysmPack(resourcePackFile);
        } else {
            this.reResourcePack = this.resourcePackFile.isDirectory() ? new FolderResourcePack(this.resourcePackFile) : new FileResourcePack(this.resourcePackFile);
        }
    }

    @Inject(method = "getTexturePackDescription", at = @At("HEAD"), cancellable = true)
    private void yeah(CallbackInfoReturnable<String> cir) {
        if (isRysmFolder()) {
            cir.setReturnValue(" ");
        }
    }

    @Inject(method = "func_183027_f", at = @At("HEAD"), cancellable = true)
    private void yeah1(CallbackInfoReturnable<Integer> cir) {
        if (isRysmFolder()) {
            cir.setReturnValue(1);
        }
    }

    public boolean isRysmFolder() {
        return resourcePackFile.isDirectory() && new File(resourcePackFile, "folder.json").isFile();
    }

     */
}
