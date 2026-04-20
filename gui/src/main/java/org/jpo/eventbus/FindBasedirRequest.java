package org.jpo.eventbus;

/*
 Copyright (C) 2022-2024 Richard Eigenmann.
 This program is free software; you can redistribute it and/or
 modify it under the terms of the GNU General Public License
 as published by the Free Software Foundation; either version 2
 of the License, or any later version. This program is distributed 
 in the hope that it will be useful, but WITHOUT ANY WARRANTY.
 Without even the implied warranty of MERCHANTABILITY or FITNESS
 FOR A PARTICULAR PURPOSE.  See the GNU General Public License for 
 more details. You should have received a copy of the GNU General Public License
 along with this program; if not, write to the Free Software
 Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 The license is in gpl.txt.
 See http://www.gnu.org/copyleft/gpl.html for the details.
 */

import org.jetbrains.annotations.NotNull;
import org.jpo.datamodel.PictureCollection;

/**
 * This request looks for a base directory
 *
 * @author Richard Eigenmann
 * @param pictureCollection The picture collection for which we want to find the base directory
 */
public record FindBasedirRequest(@NotNull PictureCollection pictureCollection) {
}
