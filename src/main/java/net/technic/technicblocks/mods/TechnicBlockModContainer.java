/*
        This mod is Technic Blocks, a basic mod to allow new connected texture
        and cosmetic blocks to be created from data.
        Copyright (C) 2014, Stephen Baynham

        This program is free software: you can redistribute it and/or modify
        it under the terms of the GNU General Public License as published by
        the Free Software Foundation, either version 3 of the License, or
        (at your option) any later version.

        This program is distributed in the hope that it will be useful,
        but WITHOUT ANY WARRANTY; without even the implied warranty of
        MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
        GNU General Public License for more details.

        You should have received a copy of the GNU General Public License
        along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/

package net.technic.technicblocks.mods;

import com.google.common.collect.ImmutableList;
import com.google.common.eventbus.EventBus;
import cpw.mods.fml.client.FMLFileResourcePack;
import cpw.mods.fml.client.FMLFolderResourcePack;
import cpw.mods.fml.common.*;
import cpw.mods.fml.common.versioning.ArtifactVersion;
import cpw.mods.fml.common.versioning.DefaultArtifactVersion;
import cpw.mods.fml.common.versioning.VersionRange;

import java.io.File;
import java.security.cert.Certificate;
import java.util.*;

public class TechnicBlockModContainer implements ModContainer {

    private File source;
    private String modid;
    private String name = "";
    private String version = "";
    private ModMetadata metadata;
    private ArtifactVersion processedVersion;

    public TechnicBlockModContainer(String modid, File source) {
        this.modid = modid;
        this.source = source;
    }

    @Override
    public String getModId() {
        return modid;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getVersion() {
        return version;
    }

    @Override
    public File getSource() {
        return source;
    }

    @Override
    public ModMetadata getMetadata() {
        return metadata;
    }

    @Override
    public void bindMetadata(MetadataCollection mc) {
        metadata = mc.getMetadataForId(modid, new HashMap<String, Object>());
        name = metadata.name;
        version = metadata.version;
    }

    @Override
    public void setEnabledState(boolean enabled) {

    }

    @Override
    public Set<ArtifactVersion> getRequirements() {
        if (metadata == null || metadata.requiredMods == null)
            return new HashSet<ArtifactVersion>();
        else
            return metadata.requiredMods;
    }

    @Override
    public List<ArtifactVersion> getDependencies() {
        if (metadata == null || metadata.dependencies == null)
            return new LinkedList<ArtifactVersion>();
        else
            return metadata.dependencies;
    }

    @Override
    public List<ArtifactVersion> getDependants() {
        if (metadata == null || metadata.dependants == null)
            return new LinkedList<ArtifactVersion>();
        else
            return metadata.dependants;
    }

    @Override
    public String getSortingRules() {
        return metadata.printableSortingRules();
    }

    @Override
    public boolean registerBus(EventBus bus, LoadController controller) {
        return false;
    }

    @Override
    public boolean matches(Object mod) {
        return false;
    }

    @Override
    public Object getMod() {
        return null;
    }

    @Override
    public ArtifactVersion getProcessedVersion()
    {
        if (processedVersion == null)
        {
            processedVersion = new DefaultArtifactVersion(getModId(), getVersion());
        }
        return processedVersion;
    }

    @Override
    public boolean isImmutable() {
        return true;
    }

    @Override
    public String getDisplayVersion() {
        return getVersion();
    }

    @Override
    public VersionRange acceptableMinecraftVersionRange() {
        return Loader.instance().getMinecraftModContainer().getStaticVersionRange();
    }

    @Override
    public Certificate getSigningCertificate() {
        return null;
    }

    @Override
    public Map<String, String> getCustomModProperties() {
        return EMPTY_PROPERTIES;
    }

    @Override
    public Class<?> getCustomResourcePackClass() {
        return getSource().isDirectory() ? FMLFolderResourcePack.class : FMLFileResourcePack.class;
    }

    @Override
    public Map<String, String> getSharedModDescriptor() {
        return null;
    }

    @Override
    public Disableable canBeDisabled() {
        return Disableable.NEVER;
    }

    @Override
    public String getGuiClassName() {
        return null;
    }

    @Override
    public List<String> getOwnedPackages() {
        return ImmutableList.of();
    }

    @Override
    public String toString()
    {
        return getModId();
    }
}
