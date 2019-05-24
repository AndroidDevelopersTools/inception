/*
 * Copyright 2019
 * Ubiquitous Knowledge Processing (UKP) Lab
 * Technische Universität Darmstadt
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.tudarmstadt.ukp.inception.support.test.recommendation;

import static org.apache.uima.util.TypeSystemUtil.typeSystem2TypeSystemDescription;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.uima.UIMAException;
import org.apache.uima.cas.CAS;
import org.apache.uima.cas.Feature;
import org.apache.uima.cas.Type;
import org.apache.uima.cas.text.AnnotationFS;
import org.apache.uima.fit.util.CasUtil;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.cas.TOP;
import org.apache.uima.resource.metadata.TypeDescription;
import org.apache.uima.resource.metadata.TypeSystemDescription;

import de.tudarmstadt.ukp.clarin.webanno.api.AnnotationSchemaService;
import de.tudarmstadt.ukp.clarin.webanno.api.dao.AnnotationSchemaServiceImpl;

public class RecommenderHelper
{

    public static void addScoreFeature(CAS aCas, String aTypeName, String aFeatureName)
            throws IOException, UIMAException
    {
        TypeSystemDescription tsd = typeSystem2TypeSystemDescription(aCas.getTypeSystem());
        TypeDescription typeDescription = tsd.getType(aTypeName);
        typeDescription.addFeature(aFeatureName + "_score", "Confidence feature", CAS.TYPE_NAME_DOUBLE);
        typeDescription.addFeature("predicted", "Is prediction", CAS.TYPE_NAME_BOOLEAN);

        AnnotationSchemaService annotationSchemaService = new AnnotationSchemaServiceImpl();
        annotationSchemaService.upgradeCas(aCas, tsd);
    }

    public static double getScore(AnnotationFS aAnnotationFS, String aFeatureName)
    {
        Feature feature = aAnnotationFS.getType().getFeatureByBaseName(aFeatureName + "_score");
        return aAnnotationFS.getDoubleValue(feature);
    }

    public static <T extends TOP> List<T> getPredictions(CAS aCas, Class<T> aClass)
            throws Exception
    {
        Type type = CasUtil.getType(aCas, aClass);
        Feature feature = type.getFeatureByBaseName("predicted");

        return JCasUtil.select(aCas.getJCas(), aClass).stream()
                .filter(fs -> fs.getBooleanValue(feature))
                .collect(Collectors.toList());
    }

}
