package org.jpo.eventbus;

/*
 Copyright (C) 2017-2025 Richard Eigenmann.
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


import org.jpo.datamodel.PictureCollection;
import org.jspecify.annotations.NonNull;

/**
 * This request indicates that the user wants open the category editor
 *
 * @author Richard Eigenmann
 */
public record OpenCategoryEditorRequest(@NonNull PictureCollection pictureCollection) {}