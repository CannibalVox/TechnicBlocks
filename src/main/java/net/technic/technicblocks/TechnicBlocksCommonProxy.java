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

package net.technic.technicblocks;

import net.technic.technicblocks.creativetabs.CreativeTabFactory;
import net.technic.technicblocks.parser.ServerParseException;

public class TechnicBlocksCommonProxy {
    public RuntimeException createParseException() {
        return new ServerParseException();
    }

    public RuntimeException createParseException(String message) {
        return new ServerParseException(message);
    }

    public RuntimeException createParseException(String message, Throwable cause) {
        return new ServerParseException(message, cause);
    }

    public void verifyCreativeTabs(CreativeTabFactory factory) {}

    public void refreshResources() {}
}
