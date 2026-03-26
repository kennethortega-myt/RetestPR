export interface IChartBarInfo {
  number_of_valid_votes: number;
  name_of_candidate: string;
  name_of_politic_group: string;
  url_candidate_image: string;
  urlAgrupacionImage: string;
  code_of_politic_group?: string;
  percentage_for_chart?: number;
  percentage_valid_votes?: number;
  number_of_list?: number;
  group?: number;
  lista?: number;
  percentage_of_valid_votes?: number;
  number_of_candidate?: string;
  no_have_urlAgrupacionImage?: boolean;
  items?: IChartBarInfo[];
}
