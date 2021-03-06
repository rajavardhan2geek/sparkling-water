package org.apache.spark.ml.h2o

import hex.deeplearning.{DeepLearningModel, DeepLearning}
import hex.deeplearning.DeepLearningModel.DeepLearningParameters
import org.apache.spark.h2o.H2OContext
import org.apache.spark.ml.{Estimator, Model}
import org.apache.spark.ml.param.ParamMap
import org.apache.spark.ml.param.{Param,Params}
import org.apache.spark.sql.DataFrame
import org.apache.spark.sql.types.StructType

/**
 * Deep learning ML component.
 */
class H2ODeepLearningModel(
                          override val parent: H2ODeepLearning,
                          override val fittingParamMap: ParamMap,
                          model: DeepLearningModel)
  extends Model[H2ODeepLearningModel] {

  override def transform(dataset: DataFrame, paramMap: ParamMap): DataFrame = ???

  override def transformSchema(schema: StructType, paramMap: ParamMap): StructType = ???
}

class H2ODeepLearning()
                     (implicit hc: H2OContext)
  extends Estimator[H2ODeepLearningModel] with HasDeepLearningParams {

  override def fit(dataset: DataFrame, paramMap: ParamMap): H2ODeepLearningModel = {
    // Verify parameters - useless here
    transformSchema(dataset.schema, paramMap, logging = true)
    import hc._
    val map = this.paramMap ++ paramMap
    val params = map(deepLearningParams)
    params._train = dataset
    val model = new DeepLearning(params).trainModel().get()
    params._train.remove()
    val dlm = new H2ODeepLearningModel(this, paramMap, model)
    dlm
  }

  override def transformSchema(schema: StructType, paramMap: ParamMap): StructType = ???
}

trait HasDeepLearningParams extends Params {
  val deepLearningParams: Param[DeepLearningParameters] = new Param(this,
    "deepLearningParams", "H2O's DeepLearning parameters", Some(new DeepLearningParameters))
  def getDeepLearningParams: DeepLearningParameters = get(deepLearningParams)

  protected def validateAndTransformSchema(
                                          schema: StructType,
                                          paramMap: ParamMap
                                            ): StructType = {
    val map = this.paramMap ++ paramMap
    ???
  }
}

