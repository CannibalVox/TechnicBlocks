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

package net.technic.technicblocks.parser;

import cpw.mods.fml.client.CustomModLoadingErrorDisplayException;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiErrorScreen;

import java.util.Collection;
import java.util.LinkedList;

public class ParseException extends CustomModLoadingErrorDisplayException {
    private static final long serialVersionUID = 1L;
    private final Throwable cause;
    private final String message;

    public ParseException(String message, Throwable cause) {
        this.cause = cause;
        this.message = message;
    }

    public ParseException(Throwable cause) {
        this(null, cause);
    }

    public ParseException(String message) {
        this(message, null);
    }

    public ParseException() {
        this(null, null);
    }

    @Override
    public void initGui(GuiErrorScreen errorScreen, FontRenderer fontRenderer) {

    }

    @Override
    public void drawScreen(GuiErrorScreen errorScreen, FontRenderer fontRenderer, int mouseRelX, int mouseRelY, float tickTime) {
        int y = 50;
        Collection<Throwable> visitedThrowables = new LinkedList<Throwable>();

        Throwable currentThrowable = this;
        while (currentThrowable != null && !visitedThrowables.contains(currentThrowable)) {
            fontRenderer.drawString(currentThrowable.getMessage(), 20, y, 0xFFFFFFFF);
            y += 30;

            visitedThrowables.add(currentThrowable);
            currentThrowable = currentThrowable.getCause();
        }
    }

    @Override
    public synchronized Throwable getCause() {
        return this.cause;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
