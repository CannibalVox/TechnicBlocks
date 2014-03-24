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

    private String thisMessage;
    private Throwable thisCause;

    public ParseException(String message, Throwable cause) {
        this.thisMessage = message;
        this.thisCause = cause;
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
        int y = 10;
        Collection<Throwable> visitedThrowables = new LinkedList<Throwable>();

        Throwable currentThrowable = this;
        while (currentThrowable != null && !visitedThrowables.contains(currentThrowable)) {
            String message = currentThrowable.getMessage();

            if (message == null) {
                message = currentThrowable.toString();
            }

            fontRenderer.drawSplitString(message, 10, y, errorScreen.width - 20, 0xFFFFFFFF);
            y += 30;

            visitedThrowables.add(currentThrowable);
            currentThrowable = currentThrowable.getCause();
        }
    }

    @Override
    public String getMessage() {
        return thisMessage;
    }

    @Override
    public synchronized Throwable getCause() {
        return thisCause;
    }
}
