/*    
    This file is part of jME Planet Demo.

    jME Planet Demo is free software: you can redistribute it and/or modify
    it under the terms of the GNU Lesser General Public License as published by
    the Free Software Foundation.

    jME Planet Demo is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Lesser General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with jME Planet Demo.  If not, see <http://www.gnu.org/licenses/>.
*/

uniform float fCloudHeight;
 
varying vec3 fvNormal;
varying vec3 fvViewDirection;
varying vec3 fvLightDirection;

void main(void)
{
   float atmoSizeFact = 1.0 + fCloudHeight*10.0;
   mat4 atmoSizeMat = mat4(atmoSizeFact);
   atmoSizeMat[3].w = 1.0;

   mat4 newWorldMatrix = gl_ModelViewMatrix * atmoSizeMat;
   gl_Position = gl_ProjectionMatrix * newWorldMatrix * gl_Vertex;
  
   vec3 vertexPosition = (gl_ModelViewMatrix * gl_Vertex).xyz;
  
   fvViewDirection  = -vertexPosition;
   fvLightDirection = gl_LightSource[0].position.xyz - vertexPosition;
   fvNormal         = gl_NormalMatrix * gl_Normal;

   gl_FrontColor = gl_Color;
}