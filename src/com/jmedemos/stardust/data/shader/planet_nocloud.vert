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
varying vec3 vLightDirectionInTangentSpace;
varying vec3 vViewDirectionInTangentSpace;

varying vec2 vTexCoords;

vec3 tangentAt(vec3 normal) {
	vec3 c1 = cross(normal, vec3(0.0, 1.0, 0.0));
	vec3 c2 = cross(normal, vec3(0.0, 0.0, 1.0));
	
	if(length(c1)>length(c2))
	{
		return c1;
	}
	
	return c2;
}

void main(void)
{
   gl_Position = ftransform();
   vTexCoords = gl_MultiTexCoord0.xy;

   vec3 vNormal = normalize(gl_Normal);

   vec3 binormal = tangentAt(vNormal);
   vec3 tangent  = cross(vNormal, binormal);
   
   vNormal              = gl_NormalMatrix * vNormal;
   vec3 vBinormal       = gl_NormalMatrix * binormal;
   vec3 vTangent        = gl_NormalMatrix * tangent;

   vec3 vertexPosition = ( gl_ModelViewMatrix * gl_Vertex ).xyz;

   vec3 vViewDirectionInWorldSpace =  ( -vertexPosition );
   vec3 vLightDirectionInWorldSpace = gl_LightSource[0].position.xyz - vertexPosition;
   
   vLightDirectionInTangentSpace.x = dot(vLightDirectionInWorldSpace, vTangent);
   vLightDirectionInTangentSpace.y = dot(vLightDirectionInWorldSpace, vBinormal);
   vLightDirectionInTangentSpace.z = dot(vLightDirectionInWorldSpace, vNormal);
   
   vViewDirectionInTangentSpace.x = dot(vViewDirectionInWorldSpace, vTangent);
   vViewDirectionInTangentSpace.y = dot(vViewDirectionInWorldSpace, vBinormal);
   vViewDirectionInTangentSpace.z = dot(vViewDirectionInWorldSpace, vNormal);
}